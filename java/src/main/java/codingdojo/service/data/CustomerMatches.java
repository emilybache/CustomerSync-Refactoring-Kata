package codingdojo.service.data;

import codingdojo.model.Customer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomerMatches<CustomerT extends Customer> {

    private final List<CustomerT> duplicates = new ArrayList<>();
    private final CustomerT customer;

    public CustomerMatches() {
        this.customer = null;
    }

    public void addDuplicate(CustomerT duplicate) {
        duplicates.add(duplicate);
    }

}
