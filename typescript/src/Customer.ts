import {Address} from "@/Address";
import {ShoppingList} from "@/ShoppingList";
import {CustomerType} from "@/CustomerType";

export class Customer {
  constructor(public externalId: string | null,
              public masterExternalId: string | null,
              public address: Address | null = null,
              public preferredStore: string | null = null,
              public internalId: string | null = null,
              public name: string | null = null,
              public customerType: CustomerType | null = null,
              public companyNumber: string | null = null,
              public shoppingLists: ShoppingList[] = []) {
  }

  addShoppingList(consumerShoppingList: ShoppingList) {
    this.shoppingLists = [...this.shoppingLists, consumerShoppingList];
  }

  // Entity based equality
  isEqual(other: Customer) {
    return this.identityKey() === other.identityKey();
  }

  // May be used as Map keys
  identityKey() {
    return `${this.externalId}-${this.masterExternalId}-${this.companyNumber}`;
  }
}
