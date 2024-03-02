package codingdojo.data;

import codingdojo.domain.entity.Customer;
import codingdojo.domain.entity.CustomerRepository;

public class CustomerDataAccess implements CustomerRepository {

    private final CustomerRepository customerDataLayer;

    public CustomerDataAccess(CustomerRepository customerDataLayer) {
        this.customerDataLayer = customerDataLayer;
    }


    @Override
    public Customer updateCustomerRecord(Customer customer) {
        return customerDataLayer.updateCustomerRecord(customer);
    }

    @Override
    public Customer createCustomerRecord(Customer customer) {
        return customerDataLayer.createCustomerRecord(customer);
    }

    @Override
    public Customer findByExternalId(String externalId) {
        return customerDataLayer.findByExternalId(externalId);
    }

    @Override
    public Customer findByMasterExternalId(String externalId) {
        return customerDataLayer.findByMasterExternalId(externalId);
    }

    @Override
    public Customer findByCompanyNumber(String companyNumber) {
        return customerDataLayer.findByCompanyNumber(companyNumber);
    }
}
