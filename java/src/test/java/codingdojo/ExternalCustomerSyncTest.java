package codingdojo;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;


public class ExternalCustomerSyncTest {

    @Test
    public void testSyncByExternalId(){
        String externalId = "12345";

        ExternalCustomer externalCustomer = createExternalCustomer();
        externalCustomer.setExternalId(externalId);

        Customer customer = new Customer();
        customer.setExternalId(externalId);
        customer.setCompanyNumber(externalCustomer.getCompanyNumber());
        customer.setCustomerType(CustomerType.COMPANY);
        customer.setInternalId("45435");

        FakeDatabase db = new FakeDatabase();
        db.addCustomer(customer);
        CustomerSync sut = new CustomerSync(db);

        StringBuilder toAssert = new StringBuilder();
        toAssert.append("BEFORE:\n");
        toAssert.append(db.printContents());

        toAssert.append("\nSYNCING THIS:\n");
        toAssert.append(ConsumerPrinter.print(externalCustomer, ""));

        // ACT
        boolean created = sut.syncWithDataLayer(externalCustomer);

        assertFalse(created);
        toAssert.append("\nAFTER:\n");
        toAssert.append(db.printContents());
        Approvals.verify(toAssert);
    }

    private ExternalCustomer createExternalCustomer() {
        ExternalCustomer externalCustomer = new ExternalCustomer();
        externalCustomer.setExternalId("12345");
        externalCustomer.setName("Standard External Customer");
        externalCustomer.setAddress(new Address("123 main st", "Helsingborg", "SE-123 45"));
        externalCustomer.setCompanyNumber("470813-8895");
        externalCustomer.setPreferredStore("Nordstan");
        externalCustomer.setShoppingLists(Arrays.asList(new ShoppingList("lipstick", "blusher")));
        return externalCustomer;
    }
}
