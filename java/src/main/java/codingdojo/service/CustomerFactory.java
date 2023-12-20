package codingdojo.service;

import codingdojo.model.Customer;

public interface CustomerFactory<CustomerT extends Customer> {

    CustomerT build();

}
