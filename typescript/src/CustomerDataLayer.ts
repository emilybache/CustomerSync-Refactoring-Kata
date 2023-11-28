import {Customer} from "@/Customer";
import {ShoppingList} from "@/ShoppingList";

export interface CustomerDataLayer {
  updateCustomerRecord(customer: Customer): Promise<Customer | null>;
  createCustomerRecord(customer: Customer): Promise<Customer>;
  updateShoppingList(consumerShoppingList: ShoppingList): Promise<void>;
  findByExternalId(externalId: string): Promise<Customer | null>;
  findByMasterExternalId(externalId: string): Promise<Customer | null>;
  findByCompanyNumber(companyNumber: string | null): Promise<Customer | null>;
}
