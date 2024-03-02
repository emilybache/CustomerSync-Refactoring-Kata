package codingdojo;

import java.util.Arrays;
import java.util.List;

/// TODO this class is shared between from the domain perspective, also the dto perspective, that means we need to have different objects for dto and domain in the future
public class ShoppingList {
    private final List<String> products;

    public ShoppingList(String... products) {
        this.products = Arrays.asList(products);
    }

    public List<String> getProducts() {
        return products;
    }

}
