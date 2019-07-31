package codingdojo;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


public class CustomerSyncTest {

    @Test
    public void syncCompanyByExternalId(){
        String externalId = "12345";

        ExternalCustomer externalCustomer = createExternalCompany();
        externalCustomer.setExternalId(externalId);

        Customer customer = createCustomerWithSameCompanyAs(externalCustomer);
        customer.setExternalId(externalId);

        CustomerDataLayer db = mock(CustomerDataLayer.class);
        when(db.findByExternalId(externalId)).thenReturn(customer);
        CustomerSync sut = new CustomerSync(db);

        // ACT
        boolean created = sut.syncWithDataLayer(externalCustomer);

        assertFalse(created);
        verify(db, atLeastOnce()).updateCustomerRecord(any(Customer.class));

    }


    private ExternalCustomer createExternalCompany() {
        ExternalCustomer externalCustomer = new ExternalCustomer();
        externalCustomer.setExternalId("12345");
        externalCustomer.setName("Acme Inc.");
        externalCustomer.setAddress(new Address("123 main st", "Helsingborg", "SE-123 45"));
        externalCustomer.setCompanyNumber("470813-8895");
        externalCustomer.setShoppingLists(Arrays.asList(new ShoppingList("lipstick", "blusher")));
        return externalCustomer;
    }

    private Customer createCustomerWithSameCompanyAs(ExternalCustomer externalCustomer) {
        Customer customer = new Customer();
        customer.setCompanyNumber(externalCustomer.getCompanyNumber());
        customer.setCustomerType(CustomerType.COMPANY);
        customer.setInternalId("45435");
        return customer;
    }

}
