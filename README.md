Consumer Match Kata 
====================

Some horrible code to refactor. Concentrate on the "ConsumerSync" class. The purpose of the 'syncWithDataLayer' method is to take a Consumer instance, and see whether there is a matching Customer in the database. If there is not, create a new Customer to match the incoming Consumer. If there is one, update it. If there are several matching Customers in our database, update them all (slightly differently).

There is a unit test there to start you off. It gives you a basic amount of coverage but has a rather weak assertion.
