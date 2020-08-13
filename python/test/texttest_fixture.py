import dbtext, os

import sqlite3

from customer_data_access import CustomerDataAccess
from customer_sync import CustomerSync, ConflictException
from model_objects import ExternalCustomer


def main():
    testdbname = "ttdb_" + str(os.getpid())  # some temporary name not to clash with other tests
    with dbtext.Sqlite3_DBText(testdbname) as db:
        # Arrange
        db.create(sqlfile="empty_db.sql")

        # Act
        with open("incoming.json", "r") as f:
            externalRecord = ExternalCustomer.from_json(f.read())

        conn = sqlite3.connect(f"{testdbname}.db")
        customerSync = CustomerSync(CustomerDataAccess(conn))

        try:
            customerSync.syncWithDataLayer(externalRecord)
        except ConflictException as e:
            print(f"ConflictException: {e}")

        # Assert
        db.dumptables("csync", "*", usemaxcol="")

if __name__ == "__main__":
    main()