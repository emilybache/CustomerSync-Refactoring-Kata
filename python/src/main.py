from customer_data_access import CustomerDataAccess
from customer_sync import CustomerSync
from model_objects import ExternalCustomer, Address, ShoppingList, Customer, CustomerType


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
    externalId = "12345"
    externalRecord = ExternalCustomer(externalId=externalId,
                                      name="Acme Inc.",
                                      isCompany=True,
                                      companyNumber="470813-8895",
                                      preferredStore="Nordstan",
                                      postalAddress=Address("123 main st", "Helsingborg", "SE-123 45"),
                                      shoppingLists=[ShoppingList(products=["lipstick", "blusher"])]
                                      )
    customer = Customer(customerType=CustomerType.COMPANY, companyNumber="32423-342", internalId="45435", externalId=externalId)

    fakeDb = FakeDatabase(customer)
    customerSync = CustomerSync(CustomerDataAccess(fakeDb))
    customerSync.syncWithDataLayer(externalRecord)

if __name__ == "__main__":
    main()