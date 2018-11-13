package codingdojo;

import java.util.Arrays;
import java.util.List;

public class ShoppingList {
    private final List<String> products;

    public ShoppingList(String... products) {
        this.products = Arrays.asList(products);
    }
}
