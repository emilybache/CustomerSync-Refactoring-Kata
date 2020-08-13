"""
Create the db text files based on the legacy database
"""

import dbtext
import sqlite3

if __name__ == "__main__":
    with sqlite3.connect(f"../src/legacy.db") as conn:
        testdb = dbtext.Sqlite3_DBText("", conn)
        testdb.write_data(".", use_master_connection=True) # creates a directory called db_tables
