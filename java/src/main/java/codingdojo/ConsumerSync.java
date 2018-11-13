package codingdojo;

import java.util.List;

public class ConsumerSync {

    private final CustomerDataLayer customerDataLayer;

    public ConsumerSync(CustomerDataLayer customerDataLayer) {
        this.customerDataLayer = customerDataLayer;
    }

    public boolean syncWithDataLayer(Consumer consumer) {

        CustomerMatches customerMatches;
        if (consumer.isCompany()) {
            customerMatches = loadCompany(consumer);
        } else {
            customerMatches = loadPerson(consumer);
        }
        Customer customer = customerMatches.getCustomer();

        if (customer == null) {
            customer = new Customer();
            customer.setExternalId(consumer.getExternalId());
            customer.setMasterExternalId(consumer.getExternalId());
        }

        populateFields(consumer, customer);

        boolean created = false;
        if (customer.getInternalId() == null) {
            customer = createCustomer(customer);
            created = true;
        } else {
            updateCustomer(customer);
        }
        updateContactInfo(consumer, customer);

        if (customerMatches.hasDuplicates()) {
            for (Customer duplicate : customerMatches.getDuplicates()) {
                updateDuplicate(consumer, duplicate);
            }
        }

        updateRelations(consumer, customer);
        updatePreferredStore(consumer, customer);

        return created;
    }

    private void updateRelations(Consumer consumer, Customer customer) {
        List<ShoppingList> consumerShoppingLists = consumer.getShoppingLists();
        List<ShoppingList> customerShoppingLists = customer.getShoppingLists();
        for (ShoppingList consumerShoppingList : consumerShoppingLists) {
            if (!customerShoppingLists.contains(consumerShoppingList)) {
                this.customerDataLayer.updateShoppingList(customer.getName(), consumerShoppingList);
            }
        }
    }

    private Customer updateCustomer(Customer customer) {
        return this.customerDataLayer.updateCustomerRecord(customer);
    }

    private void updateDuplicate(Consumer consumer, Customer duplicate) {
        if (duplicate == null) {
            duplicate = new Customer();
            duplicate.setExternalId(consumer.getExternalId());
            duplicate.setMasterExternalId(consumer.getExternalId());
        }

        populateFields(consumer, duplicate);

        if (duplicate.getInternalId() == null) {
            createCustomer(duplicate);
        } else {
            updateCustomer(duplicate);
        }
    }

    private void updatePreferredStore(Consumer consumer, Customer customer) {
        customer.setPreferredStore(consumer.getPreferredStore());
    }

    private Customer createCustomer(Customer customer) {
        return this.customerDataLayer.createCustomerRecord(customer);
    }

    private void populateFields(Consumer consumer, Customer customer) {
        customer.setName(consumer.getName());
        this.customerDataLayer.createConsumerMappingRecord(consumer, customer);
    }

    private void updateContactInfo(Consumer consumer, Customer customer) {
        customer.setAddress(consumer.getPostalAddress());
    }

    public CustomerMatches loadCompany(Consumer consumer) {

        final String externalId = consumer.getExternalId();
        final String companyNumber = consumer.getCompanyNumber();

        CustomerMatches customerMatches = customerDataLayer.loadCompanyCustomer(externalId, companyNumber);

        if (!CustomerType.COMPANY.equals(customerMatches.getCustomer().getCustomerType())) {
            throw new ConflictException("Existing customer for consumer " + externalId + " already exists and is not a company");
        }

        if ("ExternalId".equals(customerMatches.getMatchTerm())) {
            String customerCompanyNumber = customerMatches.getCustomer().getCompanyNumber();
            if (!companyNumber.equals(customerCompanyNumber)) {
                throw new ConflictException("Existing customer for consumer " + externalId + " doesn't match company number " + companyNumber + " instead found " + customerCompanyNumber);
            }
        } else if ("CompanyNumber".equals(customerMatches.getMatchTerm())) {
            String customerExternalId = customerMatches.getCustomer().getExternalId();
            if (customerExternalId != null && !externalId.equals(customerExternalId)) {
                throw new ConflictException("Existing customer for consumer " + companyNumber + " doesn't match external id " + externalId + " instead found " + customerExternalId );
            }
            Customer customer = customerMatches.getCustomer();
            customer.setExternalId(externalId);
            customer.setMasterExternalId(externalId);
        }

        return customerMatches;
    }

    public CustomerMatches loadPerson(Consumer consumer) {
        final String externalId = consumer.getExternalId();

        CustomerMatches customerMatches = customerDataLayer.loadPersonCustomer(externalId);

        if (!CustomerType.PERSON.equals(customerMatches.getCustomer().getCustomerType())) {
            throw new ConflictException("Existing customer for consumer " + externalId + " already exists and is not a person");
        }

        if (!"ExternalId".equals(customerMatches.getMatchTerm())) {
            Customer customer = customerMatches.getCustomer();
            customer.setExternalId(externalId);
            customer.setMasterExternalId(externalId);
        }

        return customerMatches;
    }
}
