package codingdojo;

import codingdojo.data.CustomerDataAccess;
import codingdojo.domain.entity.Customer;
import codingdojo.domain.entity.CustomerType;
import codingdojo.domain.entity.CustomerRepository;
import codingdojo.dto.ExternalCustomer;

public class CustomerMatchesLoader {
    private final CustomerDataAccess customerDataAccess;

    public CustomerMatchesLoader(CustomerRepository customerRepository) {
        this.customerDataAccess = new CustomerDataAccess(customerRepository);
    }

    public CustomerMatches load(ExternalCustomer externalCustomer) {
        if (externalCustomer.isCompany()) {
            return loadCompany(externalCustomer);
        } else {
            return loadPerson(externalCustomer);
        }
    }

    private CustomerMatches loadCompany(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();
        final String companyNumber = externalCustomer.getCompanyNumber();

        CustomerMatches customerMatches = loadCustomer(externalId, companyNumber);

        validateCompanyCustomer(customerMatches, externalId, companyNumber);

        return customerMatches;
    }

    private CustomerMatches loadCustomer(String externalId, String companyNumber) {
        CustomerMatches matches = new CustomerMatches();
        Customer matchByExternalId = customerDataAccess.findByExternalId(externalId);
        if (matchByExternalId != null) {
            matches.setCustomer(matchByExternalId);
            matches.setMatchTerm("ExternalId");
            Customer matchByMasterId = customerDataAccess.findByMasterExternalId(externalId);
            if (matchByMasterId != null) matches.addDuplicate(matchByMasterId);
        } else {
            Customer matchByCompanyNumber = customerDataAccess.findByCompanyNumber(companyNumber);
            if (matchByCompanyNumber != null) {
                matches.setCustomer(matchByCompanyNumber);
                matches.setMatchTerm("CompanyNumber");
            }
        }
        return matches;
    }

    private void validateCompanyCustomer(CustomerMatches customerMatches, String externalId, String companyNumber) {
        Customer customer = customerMatches.getCustomer();
        if (customer != null && !CustomerType.COMPANY.equals(customer.getCustomerType())) {
            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a company");
        }

        if ("ExternalId".equals(customerMatches.getMatchTerm())) {
            String customerCompanyNumber = customer.getCompanyNumber();
            if (!companyNumber.equals(customerCompanyNumber)) {
                customer.setMasterExternalId(null);
                customerMatches.addDuplicate(customer);
                customerMatches.setCustomer(null);
                customerMatches.setMatchTerm(null);
            }
        } else if ("CompanyNumber".equals(customerMatches.getMatchTerm())) {
            String customerExternalId = customer.getExternalId();
            if (customerExternalId != null && !externalId.equals(customerExternalId)) {
                throw new ConflictException("Existing customer for externalCustomer " + companyNumber + " doesn't match external id " + externalId + " instead found " + customerExternalId);
            }
            customer.setExternalId(externalId);
            customer.setMasterExternalId(externalId);
            customerMatches.addDuplicate(null);
        }
    }

    private CustomerMatches loadPerson(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();

        CustomerMatches customerMatches = loadPersonCustomer(externalId);

        validatePersonCustomer(customerMatches, externalId);

        return customerMatches;
    }

    private CustomerMatches loadPersonCustomer(String externalId) {
        CustomerMatches matches = new CustomerMatches();
        Customer matchByPersonalNumber = customerDataAccess.findByExternalId(externalId);
        matches.setCustomer(matchByPersonalNumber);
        if (matchByPersonalNumber != null) matches.setMatchTerm("ExternalId");
        return matches;
    }

    private void validatePersonCustomer(CustomerMatches customerMatches, String externalId) {
        Customer customer = customerMatches.getCustomer();
        if (customer != null && !CustomerType.PERSON.equals(customer.getCustomerType())) {
            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a person");
        }

        if (customer != null && !"ExternalId".equals(customerMatches.getMatchTerm())) {
            customer.setExternalId(externalId);
            customer.setMasterExternalId(externalId);
        }
    }
}
