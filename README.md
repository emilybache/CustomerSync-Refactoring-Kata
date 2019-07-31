Consumer Match Kata 
====================

Some horrible code to refactor. Concentrate on the "CustomerSync" class. The purpose of the 'syncWithDataLayer' method is to take a ExternalCustomer instance, which has been updated in an external system, and see whether there is a matching Customer in our database. If there is not, create a new Customer to match the incoming ExternalCustomer. If there is one, update it. If there are several matching Customers in our database, update them all (slightly differently).

There is a unit test there to start you off. It gives you a basic amount of coverage but has a rather weak assertion.

Branch with_tests
-----------------

The branch 'with_tests' is an alternative starting point where there are good unit tests available, and you can get started refactoring straight away. The code coverage is not quite 100%, I believe this is due to unreachable code. Another way to use this code is to read and understand the approval testing techniques used, or to re-write the tests in another style.
