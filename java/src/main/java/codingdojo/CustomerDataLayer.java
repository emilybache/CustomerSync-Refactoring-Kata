package codingdojo;

public interface CustomerDataLayer {
    CustomerMatches loadCompanyCustomer(String externalId, String companyNumber);
    CustomerMatches loadPersonCustomer(String externalId);

    Customer updateCustomerRecord(Customer customer);

    Customer createCustomerRecord(Customer customer);

    void createConsumerMappingRecord(Consumer consumer, Customer customer);

    void updateShoppingList(String name, ShoppingList consumerShoppingList);

}
