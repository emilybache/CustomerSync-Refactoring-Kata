package codingdojo.repository;

import codingdojo.model.Customer;

public interface CustomerRepository<CustomerT extends Customer> {

    void update(CustomerT customer);

    void create(CustomerT customer);

    CustomerT findByExternalId(String externalId);

    CustomerT findByMasterExternalId(String masterExternalId);

}
