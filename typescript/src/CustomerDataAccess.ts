import {CustomerDataLayer} from "@/CustomerDataLayer";
import {CustomerMatches} from "@/CustomerMatches";
import {Customer} from "@/Customer";
import {ShoppingList} from "@/ShoppingList";

export class CustomerDataAccess {
  constructor(private readonly customerDataLayer: CustomerDataLayer) {
  }

  async loadCompanyCustomer(externalId: string, companyNumber: string | null): Promise<CustomerMatches> {
    const matches = new CustomerMatches();
    const matchByExternalId = await this.customerDataLayer.findByExternalId(externalId);
    if (matchByExternalId !== null) {
      matches.customer = matchByExternalId;
      matches.matchTerm = "ExternalId";
      const matchByMasterId = await this.customerDataLayer.findByMasterExternalId(externalId);
      if (matchByMasterId !== null) matches.addDuplicate(matchByMasterId);
    } else {
      const matchByCompanyNumber = await this.customerDataLayer.findByCompanyNumber(companyNumber);
      if (matchByCompanyNumber !== null) {
        matches.customer = matchByCompanyNumber;
        matches.matchTerm = "CompanyNumber";
      }
    }
    return matches;
  }

  async loadPersonCustomer(externalId: string): Promise<CustomerMatches> {
    const matches = new CustomerMatches();
    const matchByPersonalNumber = await this.customerDataLayer.findByExternalId(externalId);
    matches.customer = matchByPersonalNumber;
    if (matchByPersonalNumber !== null) matches.matchTerm = "ExternalId";
    return matches;
  }

  async updateCustomerRecord(customer: Customer): Promise<Customer | null> {
    return await this.customerDataLayer.updateCustomerRecord(customer);
  }

  async createCustomerRecord(customer: Customer): Promise<Customer> {
    return await this.customerDataLayer.createCustomerRecord(customer);
  }

  async updateShoppingList(customer: Customer, consumerShoppingList: ShoppingList): Promise<void> {
    customer.addShoppingList(consumerShoppingList);
    await this.customerDataLayer.updateShoppingList(consumerShoppingList);
    await this.customerDataLayer.updateCustomerRecord(customer);
  }
}
