import dbtext, os

import sqlite3
    
testdbname = "ttdb_" + str(os.getpid()) # some temporary name not to clash with other tests
with dbtext.DBText(testdbname, sqlite3=True) as db: # the name you use here will be used for the directory name in the current working directory
    # Arrange
    db.create(sqlfile="empty_db.sql")
     
    
    # Act
	with open("incoming.json", "r") as f:
        externalRecord = ExternalCustomer.from_json(f.read())
	
	conn = sqlite3.connect(f"{testdbname}.db")
    customerSync = CustomerSync(CustomerDataAccess(conn))
    customerSync.syncWithDataLayer(externalRecord)


    # Assert
    db.dumptables("users", "*", usemaxcol="") # dump changes in all the tables you're interested in. "myext" is whatever extension you want to use, probably the TextTest one 
