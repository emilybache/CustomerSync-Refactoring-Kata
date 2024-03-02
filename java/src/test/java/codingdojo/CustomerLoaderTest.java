package codingdojo;

import codingdojo.domain.entity.Customer;
import codingdojo.domain.entity.CustomerRepository;
import codingdojo.domain.entity.CustomerType;
import codingdojo.dto.ExternalCustomer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomerLoaderTest {

    private CustomerRepository customerRepository;
    private CustomerMatchesLoader sut;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        sut = new CustomerMatchesLoader(customerRepository);
    }

    @Test
    void loadCompany_ShouldReturnCustomerMatchesWithCompanyCustomer() {
        // Arrange
        ExternalCustomer externalCustomer = new ExternalCustomer();
        externalCustomer.setExternalId("12345");
        externalCustomer.setCompanyNumber("C123");

        Customer companyCustomer = Customer.from(externalCustomer);
        when(customerRepository.findByExternalId("12345")).thenReturn(companyCustomer);

        // Act
        CustomerMatches matches = sut.load(externalCustomer);

        // Assert
        assertNotNull(matches);
        assertEquals(CustomerType.COMPANY, matches.getCustomer().getCustomerType());
    }

    @Test
    void loadPerson_ShouldReturnCustomerMatchesWithPersonCustomer() {
        // Arrange
        ExternalCustomer externalCustomer = new ExternalCustomer();
        externalCustomer.setExternalId("67890");


        Customer personCustomer = Customer.from(externalCustomer);
        when(customerRepository.findByExternalId("67890")).thenReturn(personCustomer);

        // Act
        CustomerMatches matches = sut.load(externalCustomer);

        // Assert
        assertNotNull(matches);
        assertEquals(CustomerType.PERSON, matches.getCustomer().getCustomerType());
    }

    @Test
    void loadCompanyWithNewCustomer_ShouldNotReturnCustomer() {
        // Arrange
        ExternalCustomer externalCustomer = new ExternalCustomer();
        externalCustomer.setExternalId("12345");
        externalCustomer.setCompanyNumber("C123");

        when(customerRepository.findByExternalId("12345")).thenReturn(null);
        when(customerRepository.findByCompanyNumber("C123")).thenReturn(null);

        // Act
        CustomerMatches matches = sut.load(externalCustomer);

        // Assert
        assertNotNull(matches);
        assertNull(matches.getCustomer());
    }

    @Test
    void loadCompanyWithMatchingExternalIdAndMasterExternalId_ShouldSetMatchTermAndAddDuplicate() {
        // Arrange
        ExternalCustomer externalCustomer = new ExternalCustomer();
        externalCustomer.setExternalId("12345");
        externalCustomer.setCompanyNumber("C123");

        Customer existingCustomer = Customer.from(externalCustomer);
        when(customerRepository.findByExternalId("12345")).thenReturn(existingCustomer);
        when(customerRepository.findByMasterExternalId("12345")).thenReturn(existingCustomer);

        // Act
        CustomerMatches matches = sut.load(externalCustomer);

        // Assert
        assertNotNull(matches);
        assertNotNull(matches.getCustomer());
        assertEquals("12345", matches.getCustomer().getExternalId());
        assertEquals("ExternalId", matches.getMatchTerm());
        assertEquals(existingCustomer, matches.getDuplicates().stream().findFirst().get());
    }

    @Test
    void loadCompanyWithMatchingCompanyNumber_ShouldSetMatchTerm() {
        // Arrange
        ExternalCustomer externalCustomer = new ExternalCustomer();
        externalCustomer.setExternalId("12345");
        externalCustomer.setCompanyNumber("C123");

        Customer existingCustomer = Customer.from(externalCustomer);
        when(customerRepository.findByCompanyNumber("C123")).thenReturn(existingCustomer);

        // Act
        CustomerMatches matches = sut.load(externalCustomer);

        // Assert
        assertNotNull(matches);
        assertNotNull(matches.getCustomer());
        assertEquals("CompanyNumber", matches.getMatchTerm());
    }

    @Test
    void loadPersonWithMatchingExternalId_ShouldSetMatchTerm() {
        // Arrange
        ExternalCustomer externalCustomer = new ExternalCustomer();
        externalCustomer.setExternalId("67890");

        Customer existingCustomer = Customer.from(externalCustomer);
        when(customerRepository.findByExternalId("67890")).thenReturn(existingCustomer);

        // Act
        CustomerMatches matches = sut.load(externalCustomer);

        // Assert
        assertNotNull(matches);
        assertNotNull(matches.getCustomer());
        assertEquals("ExternalId", matches.getMatchTerm());
    }

    @Test
    void loadCompanyValidateCustomerType_ShouldThrowConflictException() {
        // Arrange
        ExternalCustomer externalCustomer = new ExternalCustomer();
        externalCustomer.setExternalId("12345");
        externalCustomer.setCompanyNumber("C123");

        ExternalCustomer externalCustomerPersonWithSameExternalID = new ExternalCustomer();
        externalCustomerPersonWithSameExternalID.setExternalId("12345");


        Customer existingCustomer = Customer.from(externalCustomer);
        when(customerRepository.findByExternalId("12345")).thenReturn(existingCustomer);

        // Act & Assert
        assertThrows(ConflictException.class, () -> sut.load(externalCustomerPersonWithSameExternalID));
    }


}
