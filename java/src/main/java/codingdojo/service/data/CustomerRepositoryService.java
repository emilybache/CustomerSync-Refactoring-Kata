package codingdojo.service.data;

import codingdojo.model.Customer;
import codingdojo.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class CustomerRepositoryService<CustomerT extends Customer> {

    private final CustomerRepository<CustomerT> customerRepository;

    public void createOrUpdate(CustomerT customer) {
        if (customer.getInternalId() == null) {
            customerRepository.create(customer);
        } else {
            customerRepository.update(customer);
        }
    }

}
