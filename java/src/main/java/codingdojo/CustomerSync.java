package codingdojo;

import codingdojo.domain.entity.CustomerRepository;
import codingdojo.domain.entity.Customer;
import codingdojo.dto.ExternalCustomer;

public class CustomerSync {

    private final CustomerRepository customerRepository;
    private final CustomerMatchesLoader customerMatchesRepository;

    public CustomerSync(CustomerRepository customerRepository, CustomerMatchesLoader customerMatchesRepository) {
        this.customerRepository = customerRepository;
        this.customerMatchesRepository = customerMatchesRepository;
    }

    public boolean syncWithDataLayer(ExternalCustomer externalCustomer) {
        CustomerMatches customerMatches = customerMatchesRepository.load(externalCustomer);
        Customer customer = customerMatches.getCustomer();

        if (customer == null) {
            customer = Customer.from(externalCustomer);
        } else {
            customer.updateFromExternal(externalCustomer);
        }

        boolean created = false;
        if (customer.getInternalId() == null) {
            this.customerRepository.createCustomerRecord(customer);
            created = true;
        } else {
            this.customerRepository.updateCustomerRecord(customer);
        }

        if (customerMatches.hasDuplicates()) {
            handleDuplicates(externalCustomer, customerMatches);
        }

        return created;
    }

    private void handleDuplicates(ExternalCustomer externalCustomer, CustomerMatches customerMatches) {
        for (Customer duplicate : customerMatches.getDuplicates()) {
            updateDuplicate(externalCustomer, duplicate);
        }
    }

    private void updateDuplicate(ExternalCustomer externalCustomer, Customer duplicate) {
        if (duplicate == null) {
            duplicate = Customer.from(externalCustomer);
        } else {
            duplicate.setName(externalCustomer.getName());
        }
        if (duplicate.getInternalId() == null) {
            this.customerRepository.createCustomerRecord(duplicate);
        } else {
            this.customerRepository.updateCustomerRecord(duplicate);
        }
    }
}

