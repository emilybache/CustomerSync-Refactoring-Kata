Customer Sync Refactoring Kata 
==============================

This code is inspired by some actual code I saw once. The business layer logic is not well separated from the data layer, amongst other things. Concentrate on the "CustomerSync" class. The purpose of the 'syncWithDataLayer' method is to take a ExternalCustomer instance, which has been updated in an external system, and see whether there is a matching Customer in our database. If there is not, create a new Customer to match the incoming ExternalCustomer. If there is one, update it. If there are several matching Customers in our database, update them all (slightly differently).

There is a unit test there to start you off. It uses Mockito to mock access to the database, and check the stored customer is synchronized correctly with the external customer.

The change you need to make
---------------------------

As ever, you have a goal with your refactoring. The scenario is that you have been asked to synchronize an additional field from the ExternalCustomer to the Customer. The field is 'bonusPointsBalance' and is an integer. Only private people have bonus points, not companies. Add the field and ensure that if the ExternalCustomer has a different number of points from the Customer, the balance is updated in our database.

Branch with_tests
-----------------

The branch 'with_tests' is an alternative starting point where there are good unit tests available, and you can get started refactoring straight away. These tests do not use Mockito, they replace the database with a Fake and use an Approval Testing approach to check the stored customer is synchronized correctly with the external customer. The code coverage is not quite 100%, I believe this is due to unreachable code. Another way to use this code is to read and understand the approval testing techniques used, or to re-write the tests in another style.
