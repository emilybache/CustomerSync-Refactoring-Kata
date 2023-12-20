package codingdojo;

import codingdojo.exception.ConflictException;
import codingdojo.model.*;
import codingdojo.repository.CompanyRepository;
import codingdojo.repository.ShoppingListRepository;
import codingdojo.service.sync.CompanySynchronisationService;
import codingdojo.service.sync.SyncResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static codingdojo.TestUtils.EXTERNAL_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanySynchronisationServiceTest {

    private static final String COMPANY_NUMBER = "470813-8895";

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private ShoppingListRepository shoppingListRepository;
    @InjectMocks
    private CompanySynchronisationService companySynchronisationService;

    @Test
    void should_sync_company_by_external_id() {
        // Given
        ExternalCompany externalCompany = createExternalCompany();

        Company company = createCompanyWithSameExternalIdAndCompanyNumberAs(externalCompany);

        when(companyRepository.findByExternalId(EXTERNAL_ID)).thenReturn(company);

        // When
        SyncResult result = companySynchronisationService.synchronise(externalCompany);

        // Then
        assertSame(SyncResult.UPDATED, result);
        expect_company_be_updated(externalCompany, null, externalCompany.getCompanyNumber());
        verify(shoppingListRepository).updateShoppingList(externalCompany.getShoppingLists().get(0));
    }

    @Test
    void should_sync_company_by_company_number() {
        // Given
        ExternalCompany externalCompany = createExternalCompany();

        Company company = createCompanyWithSameExternalIdAndCompanyNumberAs(externalCompany);

        when(companyRepository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(company);

        // When
         SyncResult result = companySynchronisationService.synchronise(externalCompany);

        // Then
        assertSame(SyncResult.UPDATED, result);
        expect_company_be_updated(externalCompany, EXTERNAL_ID, externalCompany.getCompanyNumber());
    }

    @Test
    void should_sync_company_by_company_number_and_null_external_id() {
        // Given
        ExternalCompany externalCompany = createExternalCompany();

        Company company = createCompanyWithSameExternalIdAndCompanyNumberAs(externalCompany);
        company.setExternalId(null);

        when(companyRepository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(company);

        // When
        SyncResult result = companySynchronisationService.synchronise(externalCompany);

        // Then
        assertSame(SyncResult.UPDATED, result);
        expect_company_be_updated(externalCompany, EXTERNAL_ID, externalCompany.getCompanyNumber());
    }

    @Test
    void should_sync_company_with_duplicates() {
        // Given
        ExternalCompany externalCompany = createExternalCompany();

        Company company1 = createCompanyWithSameExternalIdAndCompanyNumberAs(externalCompany);
        Company company2 = createCompanyWithSameExternalIdAndCompanyNumberAs(externalCompany);
        company2.setName("Different name");

        when(companyRepository.findByExternalId(EXTERNAL_ID)).thenReturn(company1);
        when(companyRepository.findByMasterExternalId(EXTERNAL_ID)).thenReturn(company2);

        // When
        SyncResult result = companySynchronisationService.synchronise(externalCompany);

        // Then
        assertSame(SyncResult.UPDATED, result);
        verify(companyRepository, times(1)).update(argThat(company -> {
            if (company == company1) {
                return matcherBody(externalCompany, null, externalCompany.getCompanyNumber(), company);
            } else {
                return false;
            }
        }));
        verify(companyRepository, times(1)).update(eq(company2));
        assertEquals(externalCompany.getName(), company2.getName());
    }

    @Test
    void should_sync_company_with_different_company_number() {
        // Given
        ExternalCompany externalCompany = createExternalCompany();

        Company company = createCompanyWithSameExternalIdAndCompanyNumberAs(externalCompany);
        company.setName("Different name");
        company.setCompanyNumber("Different number");

        when(companyRepository.findByExternalId(EXTERNAL_ID)).thenReturn(company);

        // When
        SyncResult result = companySynchronisationService.synchronise(externalCompany);

        // Then
        assertSame(SyncResult.CREATED, result);
        verify(companyRepository, times(1)).update(argThat(updatedCompany -> {
            assertEquals(externalCompany.getName(), updatedCompany.getName());
            assertEquals("Different number", updatedCompany.getCompanyNumber());
            assertEquals(CustomerType.COMPANY, updatedCompany.getCustomerType());
            return true;
        }));
    }

    @Test
    void should_not_update_company_if_type_doesnt_match() {
        // Given
        ExternalCompany externalCompany = createExternalCompany();

        Company company = createCompanyWithSameExternalIdAndCompanyNumberAs(externalCompany);
        company.setCustomerType(CustomerType.PERSON);
        when(companyRepository.findByExternalId(EXTERNAL_ID)).thenReturn(company);

        // When
        ConflictException conflictException = assertThrows(ConflictException.class, () -> companySynchronisationService.synchronise(externalCompany));

        // Then
        assertEquals("Existing customer for externalCompany 12345 already exists and is not a company", conflictException.getMessage());
    }

    @Test
    void should_not_update_company_if_external_id_doesnt_match() {
        // Given
        ExternalCompany externalCompany = createExternalCompany();

        Company company = createCompanyWithSameExternalIdAndCompanyNumberAs(externalCompany);
        company.setExternalId("2345");
        when(companyRepository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(company);

        // When
        ConflictException conflictException = assertThrows(ConflictException.class, () -> companySynchronisationService.synchronise(externalCompany));

        // Then
        assertEquals("Existing customer for externalCompany 470813-8895 doesn't match external id 12345 instead found 2345", conflictException.getMessage());
    }

    @Test
    void should_create_company() {
        // Given
        ExternalCompany externalCompany = createExternalCompany();

        // When
        SyncResult result = companySynchronisationService.synchronise(externalCompany);

        // Then
        assertSame(SyncResult.CREATED, result);
        expect_company_be_created(externalCompany, externalCompany.getCompanyNumber());
    }

    private ExternalCompany createExternalCompany() {
        return (ExternalCompany) new ExternalCompany()
                .setCompanyNumber(COMPANY_NUMBER)
                .setExternalId(TestUtils.EXTERNAL_ID)
                .setName("Acme Inc.")
                .setPostalAddress(new Address("123 main st", "Helsingborg", "SE-123 45"))
                .setShoppingLists(List.of(new ShoppingList("lipstick", "blusher")))
                ;
    }

    private Company createCompanyWithSameExternalIdAndCompanyNumberAs(ExternalCompany externalCompany) {
        return (Company) new Company()
                .setCompanyNumber(externalCompany.getCompanyNumber())
                .setAddress(new Address("ABC main st", "Xxxxx", "SE-444 45"))
                .setCustomerType(CustomerType.COMPANY)
                .setInternalId("45435")
                .setExternalId(externalCompany.getExternalId());
    }

    private void expect_company_be_updated(ExternalCompany externalCompany, String masterExternalId, String companyNumber) {
        verify(companyRepository, times(1)).update(getMatcher(externalCompany, masterExternalId, companyNumber));
    }

    private void expect_company_be_created(ExternalCompany externalCompany, String companyNumber) {
        verify(companyRepository, times(1)).create(getMatcher(externalCompany, EXTERNAL_ID, companyNumber));
    }

    private Company getMatcher(ExternalCompany externalCompany, String masterExternalId, String companyNumber) {
        return argThat(company -> matcherBody(externalCompany, masterExternalId, companyNumber, company));
    }

    private boolean matcherBody(ExternalCompany externalCompany, String masterExternalId, String companyNumber, Company company) {
        assertEquals(companyNumber, company.getCompanyNumber());
        assertEquals(CustomerType.COMPANY, company.getCustomerType());

        TestUtils.expect_customer_be_updated(externalCompany, company, masterExternalId);
        return true;
    }

}
