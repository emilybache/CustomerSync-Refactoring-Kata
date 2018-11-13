package codingdojo;

public interface CompanyDataLayer {
    CustomerMatches loadCompanyCustomer(String externalId, String companyNumber);

    Customer updateCustomerRecord(Customer customer);

    Customer createCustomerRecord(Customer customer);

    void createConsumerMappingRecord(Consumer consumer, Customer customer);

    void renameRelation(String customer, Relation consumerRelation);

    CustomerMatches loadPersonCustomer(String externalId);
}
