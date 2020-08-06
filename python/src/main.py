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
    db.executescript("""\
DROP TABLE IF EXISTS customers;
CREATE TABLE IF NOT EXISTS customers (
    internalId        INT,
    externalId        VARCHAR(10),
    masterExternalId        VARCHAR(10),
    name        VARCHAR(100),
    customerType INT,
    companyNumber VARCHAR(12),
    addressId INT,
    PRIMARY KEY (internalId),
    FOREIGN KEY (addressId) REFERENCES addresses(addressId)
);
DROP TABLE IF EXISTS addresses;
CREATE TABLE IF NOT EXISTS addresses (
        addressId        INT,
        street VARCHAR(100),
        city   VARCHAR(100),
        postalCode VARCHAR(10),
        PRIMARY KEY (addressId)
    );
DROP TABLE IF EXISTS shoppinglists;
CREATE TABLE IF NOT EXISTS shoppinglists (
        shoppinglistId INT,
        products  VARCHAR(500),
        PRIMARY KEY (shoppinglistId)
    );
DROP TABLE IF EXISTS customer_shoppinglists;
CREATE TABLE IF NOT EXISTS customer_shoppinglists (
        customerId INT,
        shoppinglistId INT,
        FOREIGN KEY (customerId) REFERENCES customers(internalId),
        FOREIGN KEY (shoppinglistId) REFERENCES shoppinglists(shoppinglistId)
    );
    """)

    db.execute("DELETE FROM customers;")
    db.execute("INSERT INTO customers VALUES ('45435', '12345', NULL, NULL, 2, '32423-3425', NULL);")
    db.execute("INSERT INTO customers VALUES ('45436', '12346', NULL, NULL, 2, '32423-3426', NULL);")

    db.execute("DELETE FROM addresses")
    conn.commit()

    conn.cursor().execute("SELECT * FROM sqlite_temp_master")
    tables = [name[0] for name in conn.cursor().fetchall()]
    print(f"found conn tables: {tables}")
    conn.close()

    conn2 = sqlite3.connect("legacy.db")

    customerSync = CustomerSync(CustomerDataAccess(conn2))
    customerSync.syncWithDataLayer(externalRecord)

    conn2.cursor().execute("SELECT name FROM sqlite_master WHERE type='table';")
    tables = [name for name in conn2.cursor().fetchall()]
    print(f"found conn2 tables: {tables}")

    db = conn2.cursor()
    dump_table(db, "customers")
    dump_table(db, "addresses")
    dump_table(db, "customer_shoppinglists")
    dump_table(db, "shoppinglists")


def dump_table(db, tablename):
    print(f"{tablename}:")
    db.execute(f'SELECT * FROM {tablename}')
    lists = db.fetchall()
    for sl in lists:
        print(sl)


if __name__ == "__main__":
    main()