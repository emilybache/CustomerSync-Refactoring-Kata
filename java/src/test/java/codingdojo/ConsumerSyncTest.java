package codingdojo;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;


public class ConsumerSyncTest {

    @Test
    public void testSyncByExternalId(){
        String externalId = "12345";

        Consumer consumer = new Consumer();
        consumer.setExternalId(externalId);
        consumer.setName("Standard Consumer");
        consumer.setAddress(new Address("123 main st", "Helsingborg", "SE-123 45"));
        consumer.setCompanyNumber("470813-8895");
        consumer.setPreferredStore("Nordstan");
        consumer.setShoppingLists(Arrays.asList(new ShoppingList("lipstick", "blusher")));

        Customer customer = new Customer();
        customer.setExternalId(externalId);
        customer.setCompanyNumber(consumer.getCompanyNumber());
        customer.setCustomerType(CustomerType.COMPANY);
        customer.setInternalId("45435");

        FakeDatabase db = new FakeDatabase();
        db.addCustomer(customer);
        ConsumerSync sut = new ConsumerSync(db);

        StringBuilder toAssert = new StringBuilder();
        toAssert.append("BEFORE:\n");
        toAssert.append(db.printContents());

        toAssert.append("\nSYNCING THIS:\n");
        toAssert.append(ConsumerPrinter.print(consumer, ""));

        // ACT
        boolean created = sut.syncWithDataLayer(consumer);

        assertFalse(created);
        toAssert.append("\nAFTER:\n");
        toAssert.append(db.printContents());
        Approvals.verify(toAssert);
    }

    private Consumer standardConsumer() {
        Consumer consumer = new Consumer();
        consumer.setExternalId("12345");
        consumer.setName("Standard Consumer");
        consumer.setAddress(new Address("123 main st", "Helsingborg", "SE-123 45"));
        consumer.setCompanyNumber("470813-8895");
        consumer.setPreferredStore("Nordstan");
        consumer.setShoppingLists(Arrays.asList(new ShoppingList("lipstick", "blusher")));
        return consumer;
    }
}
