import {CustomerDataAccess} from "@/CustomerDataAccess";
import {CustomerDataLayer} from "@/CustomerDataLayer";
import {ExternalCustomer} from "@/ExternalCustomer";
import {CustomerMatches} from "@/CustomerMatches";
import {Customer} from "@/Customer";
import {CustomerType} from "@/CustomerType";
import {ConflictException} from "@/ConflictException";

export class CustomerSync {
  constructor(private readonly customerDataAccess: CustomerDataAccess) {
  }

  static fromDataLayer(customerDataLayer: CustomerDataLayer) {
    return new CustomerSync(new CustomerDataAccess(customerDataLayer));
  }

  async syncWithDataLayer(externalCustomer: ExternalCustomer): Promise<boolean> {
    let customerMatches: CustomerMatches;
    if (externalCustomer.isCompany()) {
      customerMatches = await this.loadCompany(externalCustomer);
    } else {
      customerMatches = await this.loadPerson(externalCustomer);
    }
    let customer = customerMatches.customer;

    if (customer === null) {
      customer = new Customer(externalCustomer.externalId, externalCustomer.externalId);
    }

    this.populateFields(externalCustomer, customer);

    let created = false;
    if (customer.internalId === null) {
      customer = await this.createCustomer(customer);
      created = true;
    } else {
      await this.updateCustomer(customer);
    }
    this.updateContactInfo(externalCustomer, customer);

    if (customerMatches.hasDuplicates()) {
      for (const duplicate of customerMatches.duplicates) {
        await this.updateDuplicate(externalCustomer, duplicate);
      }
    }

    await this.updateRelations(externalCustomer, customer);
    this.updatePreferredStore(externalCustomer, customer);

    return created;
  }

  private async updateRelations(externalCustomer: ExternalCustomer, customer: Customer) {
    const consumerShoppingLists = externalCustomer.shoppingLists;
    for (const consumerShoppingList of consumerShoppingLists) {
      await this.customerDataAccess.updateShoppingList(customer, consumerShoppingList);
    }
  }

  private async updateCustomer(customer: Customer) {
    await this.customerDataAccess.updateCustomerRecord(customer);
  }

  private async updateDuplicate(externalCustomer: ExternalCustomer, duplicate: Customer | null) {
    if (!duplicate) {
      duplicate = new Customer(externalCustomer.externalId, externalCustomer.externalId);
    }

    duplicate.name = externalCustomer.name;

    if (duplicate.internalId === null) {
      await this.createCustomer(duplicate);
    } else {
      await this.updateCustomer(duplicate);
    }
  }

  private updatePreferredStore(externalCustomer: ExternalCustomer, customer: Customer) {
    customer.preferredStore = externalCustomer.preferredStore;
  }

  private createCustomer(customer: Customer) {
    return this.customerDataAccess.createCustomerRecord(customer);
  }

  private populateFields(externalCustomer: ExternalCustomer, customer: Customer) {
    customer.name = externalCustomer.name;
    if (externalCustomer.isCompany()) {
      customer.companyNumber = externalCustomer.companyNumber;
      customer.customerType = CustomerType.COMPANY;
    } else {
      customer.customerType = CustomerType.PERSON;
    }
  }

  private updateContactInfo(externalCustomer: ExternalCustomer, customer: Customer) {
    customer.address = externalCustomer.postalAddress;
  }

  private async loadCompany(externalCustomer: ExternalCustomer) {
    const externalId = externalCustomer.externalId;
    const companyNumber = externalCustomer.companyNumber;

    const customerMatches = await this.customerDataAccess.loadCompanyCustomer(externalId, companyNumber);

    if (customerMatches.customer !== null && customerMatches.customer.customerType !== CustomerType.COMPANY) {
      throw new ConflictException(`Existing customer for externalCustomer ${externalId} already exists and is not a company`);
    }

    if (customerMatches.matchTerm === "ExternalId") {
      const customerCompanyNumber = customerMatches.customer?.companyNumber;
      if (companyNumber !== customerCompanyNumber) {
        customerMatches.customer!.externalId = null;
        customerMatches.addDuplicate(customerMatches.customer!);
        customerMatches.customer = null;
        customerMatches.matchTerm = null;
      }
    } else if (customerMatches.matchTerm === "CompanyNumber") {
      const customerExternalId = customerMatches.customer?.externalId;
      if (customerExternalId !== null && externalId !== customerExternalId) {
        throw new ConflictException(`Existing customer for externalCustomer ${companyNumber} doesn't match external id ${externalId} instead found ${customerExternalId}`);
      }
      const customer = customerMatches.customer!;
      customer.externalId = externalId;
      customer.masterExternalId = externalId;
      customerMatches.addDuplicate(null);
    }

    return customerMatches;
  }

  private async loadPerson(externalCustomer: ExternalCustomer) {
    const externalId = externalCustomer.externalId;

    const customerMatches = await this.customerDataAccess.loadPersonCustomer(externalId);

    if (customerMatches.customer !== null) {
      if (customerMatches.customer.customerType !== CustomerType.PERSON) {
        throw new ConflictException(`Existing customer for externalCustomer ${externalId} already exists and is not a person`);
      }

      if (customerMatches.matchTerm !== "ExternalId") {
        const customer = customerMatches.customer;
        customer.externalId = externalId;
        customer.masterExternalId = externalId;
      }
    }

    return customerMatches;
  }
}
