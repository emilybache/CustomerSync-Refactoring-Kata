package codingdojo;

import codingdojo.domain.entity.CustomerRepository;
import codingdojo.domain.entity.Customer;
import codingdojo.domain.entity.CustomerType;
import codingdojo.dto.ExternalCustomer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class CustomerSyncTest {

    private CustomerRepository db;
    private CustomerSync sut;

    @BeforeEach
    void setUp() {
        db = mock(CustomerRepository.class);
        sut = new CustomerSync(db, new CustomerMatchesLoader(db));
    }

    @Test
    void syncCompanyByExternalId_shouldUpdateExistingCompany() {
        // Arrange
        String externalId = "12345";
        ExternalCustomer externalCustomer = createExternalCompany();
        Customer existingCustomer = createCustomerWithSameCompanyAs(externalCustomer);

        when(db.findByExternalId(externalId)).thenReturn(existingCustomer);

        // Act
        boolean created = sut.syncWithDataLayer(externalCustomer);

        // Assert
        assertFalse(created);
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(db, times(1)).updateCustomerRecord(customerCaptor.capture());
        assertEquals(existingCustomer, customerCaptor.getValue());
    }

    @Test
    void syncPrivateByExternalID_shouldUpdateExistingPrivatePerson() {
        // Arrange
        String externalId = "12346";
        ExternalCustomer externalCustomer = createExternalPrivatePerson();
        Customer existingCustomer = Customer.createPerson(externalId, "102030");

        when(db.findByExternalId(externalId)).thenReturn(existingCustomer);

        // Act
        boolean created = sut.syncWithDataLayer(externalCustomer);

        // Assert
        assertFalse(created);
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(db, times(1)).updateCustomerRecord(customerCaptor.capture());
        assertEquals(existingCustomer, customerCaptor.getValue());
    }

    @Test
    void syncNewConsumerIsCreated_shouldCreateNewCustomer() {
        // Arrange
        String externalId = "12346";
        ExternalCustomer externalCustomer = createExternalPrivatePerson();
        Customer expectedCustomerCreated = Customer.from(externalCustomer);

        when(db.findByExternalId(externalId)).thenReturn(null);

        // Act
        boolean created = sut.syncWithDataLayer(externalCustomer);

        // Assert
        assertTrue(created);
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(db, times(1)).createCustomerRecord(customerCaptor.capture());
        assertEquals(expectedCustomerCreated, customerCaptor.getValue());
    }


    /// TODO left it here just for take home evaluation purpose on comparing with created tests. but in real world, it can be removed.
    @Test
    public void syncCompanyByExternalId(){
        String externalId = "12345";

        ExternalCustomer externalCustomer = createExternalCompany();

        Customer customer = createCustomerWithSameCompanyAs(externalCustomer);

        CustomerRepository db = mock(CustomerRepository.class);
        when(db.findByExternalId(externalId)).thenReturn(customer);
        CustomerSync sut = new CustomerSync(db, new CustomerMatchesLoader(db));

        // ACT
        boolean created = sut.syncWithDataLayer(externalCustomer);

        // ASSERT
        assertFalse(created);
        ArgumentCaptor<Customer> argument = ArgumentCaptor.forClass(Customer.class);
        verify(db, atLeastOnce()).updateCustomerRecord(argument.capture());
        Customer updatedCustomer = argument.getValue();
        assertEquals(externalCustomer.getName(), updatedCustomer.getName());
        assertEquals(externalCustomer.getExternalId(), updatedCustomer.getExternalId());
        assertNull(updatedCustomer.getMasterExternalId());
        assertEquals(externalCustomer.getCompanyNumber(), updatedCustomer.getCompanyNumber());
        assertEquals(externalCustomer.getPostalAddress(), updatedCustomer.getAddress());
        assertEquals(externalCustomer.getShoppingLists(), updatedCustomer.getShoppingLists());
        assertEquals(CustomerType.COMPANY, updatedCustomer.getCustomerType());
        assertNull(updatedCustomer.getPreferredStore());
    }

    @Test
    void syncPrivateByExternalID_ShouldUpdateExistingPrivatePersonAndSetBonusPointBalance() {
        // Arrange
        String externalId = "12346";
        ExternalCustomer externalCustomer = createExternalPrivatePerson();
        Customer existingCustomer = Customer.createPerson(externalId, "102030");

        when(db.findByExternalId(externalId)).thenReturn(existingCustomer);

        // Act
        boolean created = sut.syncWithDataLayer(externalCustomer);

        // Assert
        assertFalse(created);
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(db, times(1)).updateCustomerRecord(customerCaptor.capture());
        assertEquals(existingCustomer, customerCaptor.getValue());
        assertEquals(externalCustomer.getBonusPointsBalance(), customerCaptor.getValue().getBonusPointBalance());
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

    private ExternalCustomer createExternalPrivatePerson() {
        ExternalCustomer externalCustomer = new ExternalCustomer();
        externalCustomer.setExternalId("12346");
        externalCustomer.setName("Jay");
        externalCustomer.setAddress(new Address("123 main st", "Helsingborg", "SE-123 45"));
        externalCustomer.setShoppingLists(Arrays.asList(new ShoppingList("lipstick", "blusher")));
        externalCustomer.setBonusPointsBalance(10);
        return externalCustomer;
    }

    private Customer createCustomerWithSameCompanyAs(ExternalCustomer externalCustomer) {
        return  Customer.createCompany(externalCustomer.getExternalId(), "45435", externalCustomer.getCompanyNumber());
    }

}
