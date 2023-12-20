package codingdojo.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class ShoppingList {
    private final List<String> products;

    public ShoppingList(String... products) {
        this.products = Arrays.asList(products);
    }

}
