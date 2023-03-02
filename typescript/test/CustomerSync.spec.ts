import {ExternalCustomer} from "@/ExternalCustomer";
import {Address} from "@/Address";
import {ShoppingList} from "@/ShoppingList";
import {Customer} from "@/Customer";
import {CustomerType} from "@/CustomerType";
import sinonChai from "sinon-chai";
import * as sinon from 'sinon';
import * as chai from 'chai';
import {CustomerDataLayer} from "@/CustomerDataLayer";
import {CustomerSync} from "@/CustomerSync";
import {expect} from "chai";

chai.use(sinonChai);

describe("CustomerSync", () => {
  it("syncs company by external id", async () => {
    // ARRANGE
    const externalId = "12345";

    const externalCustomer = createExternalCompany();
    externalCustomer.externalId = externalId;

    const customer = createCustomerWithSameCompanyAs(externalCustomer);
    customer.externalId = externalId;

    const db: sinon.SinonStubbedInstance<CustomerDataLayer> = {
      updateCustomerRecord: sinon.stub(),
      createCustomerRecord: sinon.stub(),
      updateShoppingList: sinon.stub(),
      findByExternalId: sinon.stub(),
      findByMasterExternalId: sinon.stub(),
      findByCompanyNumber: sinon.stub()
    };
    db.findByExternalId.withArgs(externalId).resolves(customer);
    const sut = CustomerSync.fromDataLayer(db);

    // ACT
    const created = await sut.syncWithDataLayer(externalCustomer);

    // ASSERT
    expect(created).to.eql(false);
    expect(db.updateCustomerRecord).to.be.called;
    const updatedCustomer = db.updateCustomerRecord.args[0][0];
    expect(updatedCustomer.name).to.eql(externalCustomer.name);
    expect(updatedCustomer.externalId).to.eql(externalCustomer.externalId);
    expect(updatedCustomer.masterExternalId).to.eql(null);
    expect(updatedCustomer.companyNumber).to.eql(externalCustomer.companyNumber);
    expect(updatedCustomer.address).to.eql(externalCustomer.postalAddress);
    expect(updatedCustomer.shoppingLists).to.eql(externalCustomer.shoppingLists);
    expect(updatedCustomer.customerType).to.eql(CustomerType.COMPANY);
    expect(updatedCustomer.preferredStore).to.eql(null);
  });

  const createExternalCompany = () =>
    new ExternalCustomer(
      new Address("123 main st", "Helingborg", "SE-123 45"),
      "Acme Inc.",
      null,
      [new ShoppingList("lipstick", "blusher")],
      "12345",
      "470813-8895"
    )

  const createCustomerWithSameCompanyAs = (externalCustomer: ExternalCustomer) => {
    const customer = new Customer(null, null);
    customer.companyNumber = externalCustomer.companyNumber;
    customer.customerType = CustomerType.COMPANY;
    customer.internalId = "45435";
    return customer;
  }
});
