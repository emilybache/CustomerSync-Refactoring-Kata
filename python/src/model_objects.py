from dataclasses import dataclass, field
from typing import List

from enum import Enum


class CustomerType(Enum):
    PERSON = 1
    COMPANY = 2


@dataclass
class ShoppingList:
    products: List[str] = field(default_factory=list)


@dataclass
class Address:
    street: str
    city: str
    postalCode: str


@dataclass
class ExternalCustomer:
    externalId: str
    name: str
    isCompany: bool
    companyNumber: str
    preferredStore: str
    postalAddress: Address
    shoppingLists: List[ShoppingList] = field(default_factory=list)


class Customer:
    def __init__(self, internalId: str = None, externalId: str = None, masterExternalId: str = None, name: str = None,
                 customerType: CustomerType = None, companyNumber: str = None):
        self.internalId = internalId
        self.externalId = externalId
        self.masterExternalId = masterExternalId
        self.name = name
        self.customerType = customerType
        self.companyNumber = companyNumber
        self.shoppingLists = []

    def addShoppingList(self, shoppingList):
        self.shoppingLists.append(shoppingList)
