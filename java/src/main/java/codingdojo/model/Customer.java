package codingdojo.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public abstract class Customer {
    private String externalId;
    private String masterExternalId;
    private Address address;
    private String preferredStore;
    @Setter(AccessLevel.NONE)
    private List<ShoppingList> shoppingLists = new ArrayList<>();
    private String internalId;
    private String name;
    private CustomerType customerType;

    public Customer(CustomerType customerType) {
        this.customerType = customerType;
    }

    public void addShoppingList(ShoppingList consumerShoppingList) {
        List<ShoppingList> newList = new ArrayList<>(this.shoppingLists);
        newList.add(consumerShoppingList);
        this.shoppingLists = newList;
    }

}
