package codingdojo.service.sync;

import codingdojo.model.Customer;
import codingdojo.model.ExternalCustomer;

public interface FieldsSynchronisationService<ExternalCustomerT extends ExternalCustomer, CustomerT extends Customer> {

    default void populateFields(ExternalCustomerT externalCustomer, CustomerT customer) {}

}
