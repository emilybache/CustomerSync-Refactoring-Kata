# Customer Sync Refactoring Kata in python
In this folder there's a translation of the customer sync production code into python. This is in the 'src' folder.

What is different from the Java version is that this one supports an actual database not just a fake one. The 
'CustomerDataAccess' class uses SQL commands to access a SQL database.

The 'src/database.sql' file can be used to create a SQL database. It has only one record, whereas a real database would of
course have many more. It gives you an idea of what it could be like though.

Before you will be able to use the database you will need to install ODBC and MySQL. For example on Amazon Linux:

    sudo yum install unixODBC-devel
    sudo yum install mysql-connector-odbc.x86_64    
    
Note the version of odbc you used, you may need it in your odbc connection string. Eg 5.2.5-8.amzn2

You will also need to install pyodbc and other python dependencies:

    pip install -r requirements.txt

## Test cases with TextTest and dbtext
In the 'test' subfolder there is a setup for testing this code using [TextTest](http://texttest.org) and 
[dbtext](https://github.com/texttest/dbtext).

The file 'test/test_rig.py' is the test harness script that is called by TextTest. It uses dbtext to create a
database filled with data specified in your test via text files. (It uses sqlite3 as the database engine). 
Then it calls the system under test with an incoming
record parsed from json. At the end dbtext writes the entire contents of the database into text files that will be collected by
TextTest and used to determine whether the test passed or not. If the dumped text files match the approved files then the 
test passes.
 
In the main branch there is only one test case - 'sync_by_company_number'. In the 'with_tests' branch there are more 
test cases. Each test case comprises a number of dbtext files for populating the initial database, and an 'incoming.json'
file containing the record which should be synced. Also in each test case are the approved files detailing what should
be in the database at the end of the test.
