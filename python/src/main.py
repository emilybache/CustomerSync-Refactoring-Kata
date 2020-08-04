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
    with open("database.sql", "r") as f:
        db.execute("""CREATE TABLE IF NOT EXISTS customers (
    internalId        VARCHAR(10),
    externalId        VARCHAR(10),
    masterExternalId        VARCHAR(10),
    name        VARCHAR(100),
    customerType INT,
    companyNumber VARCHAR(12),
    PRIMARY KEY (internalId)
);""")
        db.execute("DELETE FROM customers;")
        db.execute("INSERT INTO customers VALUES ('45435', '12345', NULL, NULL, 2, '32423-342');")
        conn.commit()
    #externalId = "12345"
    #customer = Customer(customerType=CustomerType.COMPANY, companyNumber="32423-342", internalId="45435", externalId=externalId)

    #fakeDb = FakeDatabase(customer)
    customerSync = CustomerSync(CustomerDataAccess(conn))
    customerSync.syncWithDataLayer(externalRecord)

if __name__ == "__main__":
    main()