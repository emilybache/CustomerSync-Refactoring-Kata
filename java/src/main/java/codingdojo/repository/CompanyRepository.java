package codingdojo.repository;

import codingdojo.model.Company;

public interface CompanyRepository extends CustomerRepository<Company> {

    Company findByCompanyNumber(String companyNumber);

}
