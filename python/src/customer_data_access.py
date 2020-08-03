from dataclasses import dataclass, field
from typing import List

from model_objects import Customer, ShoppingList


@dataclass
class CustomerMatches:
    match_term: str = None
    customer: Customer = None
    duplicates: List[Customer] = field(default_factory=list)

    def has_duplicates(self):
        return self.duplicates

    def add_duplicate(self, duplicate):
        self.duplicates.append(duplicate)


class CustomerDataAccess:
    def __init__(self, customerDataLayer):
        self.customerDataLayer = customerDataLayer

    def loadCompanyCustomer(self, externalId, companyNumber):
        matches = CustomerMatches()
        matchByExternalId: Customer = self.customerDataLayer.findByExternalId(externalId)
        if matchByExternalId is not None:
            matches.customer = matchByExternalId
            matches.matchTerm = "ExternalId"
            matchByMasterId: Customer = self.customerDataLayer.findByMasterExternalId(externalId)
            if matchByMasterId is not None:
                matches.add_duplicate(matchByMasterId)
        else:
            matchByCompanyNumber: Customer = self.customerDataLayer.findByCompanyNumber(companyNumber)
            if matchByCompanyNumber is not None:
                matches.customer = matchByCompanyNumber
                matches.matchTerm = "CompanyNumber"

        return matches

    def loadPersonCustomer(self, externalId):
        matches = CustomerMatches()
        matchByPersonalNumber: Customer = self.customerDataLayer.findByExternalId(externalId)
        matches.customer = matchByPersonalNumber
        if matchByPersonalNumber is not None:
            matches.matchTerm = "ExternalId"
        return matches

    def updateCustomerRecord(self, customer):
        self.customerDataLayer.updateCustomerRecord(customer)

    def createCustomerRecord(self, customer):
        return self.customerDataLayer.createCustomerRecord(customer)

    def updateShoppingList(self, customer: Customer, shoppingList: ShoppingList):
        customer.addShoppingList(shoppingList)
        self.customerDataLayer.updateShoppingList(shoppingList)
        self.customerDataLayer.updateCustomerRecord(customer)
