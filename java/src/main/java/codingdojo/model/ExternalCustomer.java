package codingdojo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public abstract class ExternalCustomer {
    private Address postalAddress;
    private String name;
    private String preferredStore;
    private List<ShoppingList> shoppingLists;
    private String externalId;

}
