package codingdojo.service.sync;

import codingdojo.exception.ConflictException;
import codingdojo.model.Company;
import codingdojo.model.CustomerType;
import codingdojo.model.ExternalCompany;
import codingdojo.repository.CompanyRepository;
import codingdojo.repository.ShoppingListRepository;
import codingdojo.service.data.CompanyRepositoryService;
import codingdojo.service.data.CustomerMatches;

import java.util.Optional;

public class CompanySynchronisationService {

    private final CompanyRepositoryService companyRepositoryService;
    private final BaseSynchronisationService<ExternalCompany, Company> companySynchronisationService;

    public CompanySynchronisationService(CompanyRepository companyRepository, ShoppingListRepository shoppingListRepository) {
        companyRepositoryService = new CompanyRepositoryService(companyRepository);
        companySynchronisationService = new BaseSynchronisationService<>(
                shoppingListRepository,
                companyRepositoryService,
                Company::new,
                new CompanyFieldsSynchronisationService()
        );
    }

    public SyncResult synchronise(ExternalCompany externalCompany) {
        final CustomerMatches<Company> customerMatches = companyRepositoryService.loadCompany(externalCompany.getExternalId(), externalCompany.getCompanyNumber());
        validateExternalId(externalCompany.getExternalId(), externalCompany.getCompanyNumber(), customerMatches);
        validateCustomerType(externalCompany.getExternalId(), customerMatches);
        return companySynchronisationService.synchronise(externalCompany, customerMatches);
    }

    private static void validateExternalId(String externalId, String companyNumber, CustomerMatches<Company> customerMatches) {
        String customerExternalId = Optional.ofNullable(customerMatches.getCustomer()).map(Company::getExternalId).orElse(null);
        if (customerExternalId != null && !externalId.equals(customerExternalId)) {
            throw new ConflictException("Existing customer for externalCompany " + companyNumber + " doesn't match external id " + externalId + " instead found " + customerExternalId);
        }
    }

    private static void validateCustomerType(String externalId, CustomerMatches<Company> customerMatches) {
        if (customerMatches.getCustomer() != null && CustomerType.COMPANY != customerMatches.getCustomer().getCustomerType()) {
            throw new ConflictException("Existing customer for externalCompany " + externalId + " already exists and is not a company");
        }
    }

}
