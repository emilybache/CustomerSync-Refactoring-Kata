package codingdojo;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConsumerSyncTest {

    @Test
    public void testSync(){
        Consumer consumer = standardConsumer();

        CustomerMatches customerMatches = new CustomerMatches();
        Customer customer = new Customer();
        customer.setExternalId(consumer.getExternalId());
        customer.setCompanyNumber(consumer.getCompanyNumber());
        customer.setCustomerType(CustomerType.COMPANY);
        customer.setInternalId("45435");

        customerMatches.setCustomer(customer);
        customerMatches.setMatchTerm("ExternalId");

        CustomerDataLayer db = mock(CustomerDataLayer.class);
        when(db.loadCompanyCustomer(consumer.getExternalId(), consumer.getCompanyNumber())).thenReturn(customerMatches);
        ConsumerSync sut = new ConsumerSync(db);

        boolean created = sut.syncWithDataLayer(consumer);

        assertFalse(created);
        verify(db).updateCustomerRecord(any(Customer.class));
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
