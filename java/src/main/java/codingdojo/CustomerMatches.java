package codingdojo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;

public class CustomerMatches {
    private Collection<Customer> duplicates = new ArrayList<>();
    private String matchTerm;
    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public boolean hasDuplicates() {
        return !duplicates.isEmpty();
    }

    public Collection<Customer> getDuplicates() {
        return duplicates;
    }

    public String getMatchTerm() {
        return matchTerm;
    }
}
