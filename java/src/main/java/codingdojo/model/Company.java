package codingdojo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Company extends Customer {
    private String companyNumber;

    public Company() {
        super(CustomerType.COMPANY);
    }
}
