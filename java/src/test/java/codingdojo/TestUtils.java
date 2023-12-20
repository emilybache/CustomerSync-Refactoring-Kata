package codingdojo;

import codingdojo.model.Customer;
import codingdojo.model.ExternalCustomer;
import codingdojo.model.ShoppingList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {
    public static final String EXTERNAL_ID = "12345";

    public static void expect_customer_be_updated(ExternalCustomer externalCustomer, Customer customer, String masterExternalId) {
        assertEquals(externalCustomer.getName(), customer.getName());
        assertEquals(externalCustomer.getExternalId(), customer.getExternalId());
        assertEquals(masterExternalId, customer.getMasterExternalId());
        assertEquals(externalCustomer.getPostalAddress(), customer.getAddress());
        assertEquals(externalCustomer.getShoppingLists().stream().map(ShoppingList::getProducts).toList(), customer.getShoppingLists().stream().map(ShoppingList::getProducts).toList());
        assertEquals(externalCustomer.getPreferredStore(), customer.getPreferredStore());
    }
}
