#!/usr/bin/env python

import dbtext, os

from customer_data_access import CustomerDataAccess
from customer_sync import CustomerSync, ConflictException
from model_objects import ExternalCustomer


def main():
    testdbname = "ttdb_" + str(os.getpid())  # some temporary name not to clash with other tests

    # you should be able to use dbtext.MSSQL_DBText or dbtext.Sqlite3_DBText or dbtext.MySQL_DBText depending on what you have installed
    with dbtext.Sqlite3_DBText(testdbname) as db:
        # Arrange
        db.create(sqlfile="empty_db.sql")

        # Act
        with open("incoming.json", "r") as f:
            externalRecord = ExternalCustomer.from_json(f.read())

        conn = db.make_connection(testdbname)
        customerSync = CustomerSync(CustomerDataAccess(conn))

        try:
            customerSync.syncWithDataLayer(externalRecord)
        except ConflictException as e:
            print(f"ConflictException: {e}")

        # Assert
        db.dumptables("csync", "*", exclude="trace*", usemaxcol="")

if __name__ == "__main__":
    main()