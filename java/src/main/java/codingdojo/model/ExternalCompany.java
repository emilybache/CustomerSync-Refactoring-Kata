package codingdojo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ExternalCompany extends ExternalCustomer {
    private String companyNumber;

}
