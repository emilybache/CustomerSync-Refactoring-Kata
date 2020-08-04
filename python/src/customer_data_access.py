from dataclasses import dataclass, field
from typing import List

from model_objects import Customer, ShoppingList, CustomerType


@dataclass
class CustomerMatches:
    match_term: str = None
    customer: Customer = None
    duplicates: List[Customer] = field(default_factory=list)

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
        self.cursor.execute('SELECT * FROM customers WHERE externalId=?', (externalId,))
        return self._customer_from_sql_select_fields(self.cursor.fetchone())

    def _customer_from_sql_select_fields(self, fields):
        if not fields:
            return None
        return Customer(internalId=fields[0], externalId=fields[1], masterExternalId=fields[2], name=fields[3],
                        customerType=CustomerType(fields[4]), companyNumber=fields[5])

    def findByMasterExternalId(self, masterExternalId):
        self.cursor.execute('SELECT * FROM customers WHERE masterExternalId=?', (masterExternalId,))
        return self._customer_from_sql_select_fields(self.cursor.fetchone())

    def findByCompanyNumber(self, companyNumber):
        self.cursor.execute('SELECT * FROM customers WHERE companyNumber=?', (companyNumber,))
        return self._customer_from_sql_select_fields(self.cursor.fetchone())

    def createCustomerRecord(self, customer):
        customer.internalId = "213123"
        self.cursor.execute('INSERT INTO customers VALUES (?, ?, ?, ?, ?, ?);', (customer.internalId, customer.externalId, customer.masterExternalId, customer.name, customer.customerType.value, customer.companyNumber))
        self.conn.commit()
        return customer

    def updateCustomerRecord(self, customer):
        self.cursor.execute('Update customers set externalId=?, masterExternalId=?, name=?, customerType=?, companyNumber=? WHERE internalId=?',
                            (customer.externalId, customer.masterExternalId, customer.name, customer.customerType.value, customer.companyNumber, customer.internalId))
        self.conn.commit()

    def updateShoppingList(self, shoppingList):
        pass

