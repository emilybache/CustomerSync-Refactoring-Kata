package codingdojo.domain.entity;

import codingdojo.domain.entity.Customer;

public interface CustomerRepository {

    Customer updateCustomerRecord(Customer customer);

    Customer createCustomerRecord(Customer customer);

    Customer findByExternalId(String externalId);

    Customer findByMasterExternalId(String externalId);

    Customer findByCompanyNumber(String companyNumber);
}
