package codingdojo.service.data;

import codingdojo.model.Company;
import codingdojo.repository.CompanyRepository;

public class CompanyRepositoryService extends CustomerRepositoryService<Company> {

    private final CompanyRepository companyRepository;

    public CompanyRepositoryService(CompanyRepository companyRepository) {
        super(companyRepository);
        this.companyRepository = companyRepository;
    }

    public CustomerMatches<Company> loadCompany(String externalId, String companyNumber) {
        final CustomerMatches<Company> customerMatches;

        final Company matchByExternalId = companyRepository.findByExternalId(externalId);
        if (matchByExternalId != null) {
            customerMatches = buildMatchesByExternalId(externalId, companyNumber, matchByExternalId);
        } else {
            final Company matchByCompanyNumber = companyRepository.findByCompanyNumber(companyNumber);
            if (matchByCompanyNumber != null) {
                customerMatches = buildMatchesByCompanyNumber(externalId, matchByCompanyNumber);
            } else {
                customerMatches = new CustomerMatches<>();
            }
        }

        return customerMatches;
    }

    private static CustomerMatches<Company> buildMatchesByCompanyNumber(String externalId, Company company) {
        company.setMasterExternalId(externalId);

        final CustomerMatches<Company> customerMatches = new CustomerMatches<>(company);
        customerMatches.addDuplicate(null);
        return customerMatches;
    }

    private CustomerMatches<Company> buildMatchesByExternalId(String externalId, String companyNumber, Company company) {
        final CustomerMatches<Company> customerMatches;

        if (!companyNumber.equals(company.getCompanyNumber())) {
            final Company duplicate = (Company) company.setMasterExternalId(null);
            customerMatches = new CustomerMatches<>();
            customerMatches.addDuplicate(duplicate);
        } else {
            customerMatches = new CustomerMatches<>(company);
        }

        final Company duplicateByMasterId = companyRepository.findByMasterExternalId(externalId);
        if (duplicateByMasterId != null) {
            customerMatches.addDuplicate(duplicateByMasterId);
        }

        return customerMatches;
    }


}
