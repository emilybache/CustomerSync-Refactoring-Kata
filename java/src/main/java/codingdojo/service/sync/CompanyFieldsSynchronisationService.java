package codingdojo.service.sync;

import codingdojo.model.Company;
import codingdojo.model.ExternalCompany;

public class CompanyFieldsSynchronisationService implements FieldsSynchronisationService<ExternalCompany, Company> {
    @Override
    public void populateFields(ExternalCompany externalCompany, Company customer) {
        customer.setCompanyNumber(externalCompany.getCompanyNumber());
    }
}
