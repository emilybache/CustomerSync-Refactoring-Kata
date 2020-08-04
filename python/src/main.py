from customer_data_access import CustomerDataAccess
from customer_sync import CustomerSync
from model_objects import ExternalCustomer, Address, ShoppingList, Customer, CustomerType

import sqlite3

class FakeDatabase:
    def __init__(self, customer):
        self.customer = customer
    def findByExternalId(self, externalId):
        return self.customer
    def findByMasterExternalId(self, masterExternalId):
        return self.customer
    def createCustomerRecord(self, customer):
        customer.internalId = "213123"
        return customer
    def updateCustomerRecord(self, customer):
        pass
    def updateShoppingList(self, shoppingList):
        pass

def main():
    with open("incoming.json", "r") as f:
        externalRecord = ExternalCustomer.from_json(f.read())

    conn = sqlite3.connect("legacy.db")
    db = conn.cursor()
    db.execute('DROP TABLE customers;')
    db.execute("""CREATE TABLE IF NOT EXISTS customers (
    internalId        INT,
    externalId        VARCHAR(10),
    masterExternalId        VARCHAR(10),
    name        VARCHAR(100),
    customerType INT,
    companyNumber VARCHAR(12),
    addressId INT,
    PRIMARY KEY (internalId),
    FOREIGN KEY (addressId) REFERENCES addresses(addressId)
);""")
    db.execute("""CREATE TABLE IF NOT EXISTS addresses (
        addressId        VARCHAR(10),
        street VARCHAR(100),
        city   VARCHAR(100),
        postalCode VARCHAR(10),
        PRIMARY KEY (addressId)
    );""")
    db.execute("DELETE FROM customers;")
    db.execute("INSERT INTO customers VALUES ('45435', '12345', NULL, NULL, 2, '32423-342', NULL);")
    db.execute("DELETE FROM addresses")
    conn.commit()

    customerSync = CustomerSync(CustomerDataAccess(conn))
    customerSync.syncWithDataLayer(externalRecord)

    db.execute('SELECT * FROM customers')
    customers = db.fetchall()
    print("customers:")
    for c in customers:
        print(c)
    print("addresses:")
    db.execute('SELECT * FROM addresses')
    addresses = db.fetchall()
    for a in addresses:
        print(a)

if __name__ == "__main__":
    main()