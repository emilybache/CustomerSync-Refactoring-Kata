from dataclasses import dataclass, field
from typing import List

from model_objects import Customer, ShoppingList, CustomerType, Address


class CustomerMatches:
    def __init__(self):
        self.matchTerm = None
        self.customer = None
        self.duplicates = []

    def has_duplicates(self):
        return self.duplicates

    def add_duplicate(self, duplicate):
        self.duplicates.append(duplicate)


class CustomerDataAccess:
    def __init__(self, db):
        self.customerDataLayer = CustomerDataLayer(db)

    def loadCompanyCustomer(self, externalId, companyNumber):
        matches = CustomerMatches()
        matchByExternalId: Customer = self.customerDataLayer.findByExternalId(externalId)
        if matchByExternalId is not None:
            matches.customer = matchByExternalId
            matches.matchTerm = "ExternalId"
            matchByMasterId: Customer = self.customerDataLayer.findByMasterExternalId(externalId)
            if matchByMasterId is not None:
                matches.add_duplicate(matchByMasterId)
        else:
            matchByCompanyNumber: Customer = self.customerDataLayer.findByCompanyNumber(companyNumber)
            if matchByCompanyNumber is not None:
                matches.customer = matchByCompanyNumber
                matches.matchTerm = "CompanyNumber"

        return matches

    def loadPersonCustomer(self, externalId):
        matches = CustomerMatches()
        matchByPersonalNumber: Customer = self.customerDataLayer.findByExternalId(externalId)
        matches.customer = matchByPersonalNumber
        if matchByPersonalNumber is not None:
            matches.matchTerm = "ExternalId"
        return matches

    def updateCustomerRecord(self, customer):
        self.customerDataLayer.updateCustomerRecord(customer)

    def createCustomerRecord(self, customer):
        return self.customerDataLayer.createCustomerRecord(customer)

    def updateShoppingList(self, customer: Customer, shoppingList: ShoppingList):
        customer.addShoppingList(shoppingList)
        self.customerDataLayer.updateShoppingList(shoppingList)
        self.customerDataLayer.updateCustomerRecord(customer)


class CustomerDataLayer:
    def __init__(self, conn):
        self.conn = conn
        self.cursor = self.conn.cursor()

    def findByExternalId(self, externalId):
        self.cursor.execute(
            'SELECT internalId, externalId, masterExternalId, name, customerType, companyNumber FROM customers WHERE externalId=?',
            (externalId,))
        customer = self._customer_from_sql_select_fields(self.cursor.fetchone())
        return customer

    def _find_addressId(self, customer):
        self.cursor.execute('SELECT addressId FROM customers WHERE internalId=?', (customer.internalId,))
        (addressId,) = self.cursor.fetchone()
        if addressId:
            return int(addressId)
        return None

    def _customer_from_sql_select_fields(self, fields):
        if not fields:
            return None

        customer = Customer(internalId=fields[0], externalId=fields[1], masterExternalId=fields[2], name=fields[3],
                        customerType=CustomerType(fields[4]), companyNumber=fields[5])
        addressId = self._find_addressId(customer)
        if addressId:
            self.cursor.execute('SELECT street, city, postalCode FROM addresses WHERE addressId=?',
                                          (addressId, ))
            addresses = self.cursor.fetchone()
            if addresses:
                (street, city, postalCode) = addresses
                address = Address(street, city, postalCode)
                customer.address = address
        self.cursor.execute('SELECT shoppinglistId FROM customer_shoppinglists WHERE customerId=?', (customer.internalId,))
        shoppinglists = self.cursor.fetchall()
        for sl in shoppinglists:
            self.cursor.execute('SELECT products FROM shoppinglists WHERE shoppinglistId=?', (sl[0],))
            products_as_str = self.cursor.fetchone()
            products = products_as_str[0].split(", ")
            customer.addShoppingList(ShoppingList(products))
        return customer

    def findByMasterExternalId(self, masterExternalId):
        self.cursor.execute(
            'SELECT internalId, externalId, masterExternalId, name, customerType, companyNumber FROM customers WHERE masterExternalId=?',
            (masterExternalId,))
        return self._customer_from_sql_select_fields(self.cursor.fetchone())

    def findByCompanyNumber(self, companyNumber):
        self.cursor.execute(
            'SELECT internalId, externalId, masterExternalId, name, customerType, companyNumber FROM customers WHERE companyNumber=?',
            (companyNumber,))
        return self._customer_from_sql_select_fields(self.cursor.fetchone())

    def createCustomerRecord(self, customer):
        customer.internalId = self._nextid("customers")
        self.cursor.execute('INSERT INTO customers VALUES (?, ?, ?, ?, ?, ?, ?);', (
        customer.internalId, customer.externalId, customer.masterExternalId, customer.name, customer.customerType.value,
        customer.companyNumber, None))
        if customer.address:
            addressId = self._nextid("addresses")
            self.cursor.execute('INSERT INTO addresses VALUES (?, ?, ?, ?)', (
                addressId, customer.address.street, customer.address.city, customer.address.postalCode))
            self.cursor.execute('UPDATE customers set addressId=? WHERE internalId=?', (addressId, customer.internalId))

        if customer.shoppingLists:
            for sl in customer.shoppingLists:
                data = ", ".join(sl)
                self.cursor.execute('SELECT shoppinglistId FROM shoppinglists WHERE products=?', (data,))
                shoppinglistId = self.cursor.fetchone()
                if not shoppinglistId:
                    shoppinglistId = self._nextid("shoppinglists")
                    self.cursor.execute('INSERT INTO shoppinglists VALUES (?, ?)', (shoppinglistId, data))
                self.cursor.execute('INSERT INTO customer_shoppinglists VALUES (?, ?)', (customer.internalId, shoppinglistId))
        self.conn.commit()
        return customer

    def _nextid(self, tablename):
        self.cursor.execute(f'SELECT MAX(ROWID) AS max_id FROM {tablename};')
        (id,) = self.cursor.fetchone()
        if id:
            return int(id) + 1
        else:
            return 1

    def updateCustomerRecord(self, customer):
        self.cursor.execute(
            'Update customers set externalId=?, masterExternalId=?, name=?, customerType=?, companyNumber=? WHERE internalId=?',
            (customer.externalId, customer.masterExternalId, customer.name, customer.customerType.value,
                customer.companyNumber, customer.internalId))
        if customer.address:
            addressId = self._find_addressId(customer)
            if not addressId:
                addressId = self._nextid("addresses")
                self.cursor.execute('INSERT INTO addresses VALUES (?, ?, ?, ?)', (addressId, customer.address.street, customer.address.city, customer.address.postalCode))
                self.cursor.execute('UPDATE customers set addressId=? WHERE internalId=?', (addressId, customer.internalId))

        self.cursor.execute('DELETE FROM customer_shoppinglists WHERE customerId=?', (customer.internalId,))
        if customer.shoppingLists:
            for sl in customer.shoppingLists:
                products = ", ".join(sl.products)
                self.cursor.execute('SELECT shoppinglistId FROM shoppinglists WHERE products=?', (products,))
                shoppinglistIds = self.cursor.fetchone()
                if shoppinglistIds is not None:
                    (shoppinglistId,) = shoppinglistIds
                    self.cursor.execute('INSERT INTO customer_shoppinglists VALUES (?, ?)',
                                        (customer.internalId, shoppinglistId))
                else:
                    shoppinglistId = self._nextid("shoppinglists")
                    self.cursor.execute('INSERT INTO shoppinglists VALUES (?, ?)', (shoppinglistId, products))
                    self.cursor.execute('INSERT INTO customer_shoppinglists VALUES (?, ?)', (customer.internalId, shoppinglistId))

        self.conn.commit()

    def updateShoppingList(self, shoppingList):
        pass
