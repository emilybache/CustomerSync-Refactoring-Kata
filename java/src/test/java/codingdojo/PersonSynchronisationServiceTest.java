package codingdojo;

import codingdojo.exception.ConflictException;
import codingdojo.model.*;
import codingdojo.repository.PersonRepository;
import codingdojo.repository.ShoppingListRepository;
import codingdojo.service.sync.PersonSynchronisationService;
import codingdojo.service.sync.SyncResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static codingdojo.TestUtils.EXTERNAL_ID;
import static codingdojo.TestUtils.expect_customer_be_updated;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonSynchronisationServiceTest {

    @Mock
    private PersonRepository personRepository;
    @Mock
    private ShoppingListRepository shoppingListRepository;
    @InjectMocks
    private PersonSynchronisationService personSynchronisationService;

    @Test
    void should_sync_natural_person() {
        // Given
        ExternalPerson externalPerson = createExternalPerson();

        Person person = createPersonWithSamePersonAs(externalPerson);

        when(personRepository.findByExternalId(TestUtils.EXTERNAL_ID)).thenReturn(person);

        // When
        SyncResult result = personSynchronisationService.synchronise(externalPerson);

        // Then
        assertSame(SyncResult.UPDATED, result);
        expect_person_be_updated(externalPerson);
        verify(shoppingListRepository).updateShoppingList(externalPerson.getShoppingLists().get(0));
    }

    @Test
    void should_create_natural_person() {
        // Given
        ExternalPerson externalPerson = createExternalPerson();

        // When
        SyncResult result = personSynchronisationService.synchronise(externalPerson);

        // Then
        assertSame(SyncResult.CREATED, result);
        expect_person_be_created(externalPerson);
    }

    @Test
    void should_not_update_person_if_type_doesnt_match() {
        // Given
        ExternalPerson externalPerson = createExternalPerson();

        Person person = createPersonWithSamePersonAs(externalPerson);
        person.setCustomerType(CustomerType.COMPANY);
        when(personRepository.findByExternalId(TestUtils.EXTERNAL_ID)).thenReturn(person);

        // When
        ConflictException conflictException = assertThrows(ConflictException.class, () -> personSynchronisationService.synchronise(externalPerson));

        // Then
        assertEquals("Existing customer for externalCustomer 12345 already exists and is not a person", conflictException.getMessage());
    }

    private ExternalPerson createExternalPerson() {
        return (ExternalPerson) new ExternalPerson()
                .setExternalId(EXTERNAL_ID)
                .setName("John Doe")
                .setPostalAddress(new Address("rue Blabla", "Blablange", "L-1234"))
                .setShoppingLists(List.of(new ShoppingList("toilet paper", "touth brush")))
                .setPreferredStore("TK-Maxx");
    }

    private Person createPersonWithSamePersonAs(ExternalCustomer externalCustomer) {
        return (Person) new Person()
                .setName(externalCustomer.getName())
                .setAddress(externalCustomer.getPostalAddress())
                .setCustomerType(CustomerType.PERSON)
                .setInternalId("45435")
                .setExternalId(externalCustomer.getExternalId());
    }

    private void expect_person_be_updated(ExternalPerson externalPerson) {
        verify(personRepository, times(1)).update(getMatcher(externalPerson, null));
    }

    private void expect_person_be_created(ExternalPerson externalPerson) {
        verify(personRepository, times(1)).create(getMatcher(externalPerson, TestUtils.EXTERNAL_ID));
    }

    private Person getMatcher(ExternalPerson externalPerson, String masterExternalId) {
        return argThat(person -> {
            assertEquals(CustomerType.PERSON, person.getCustomerType());

            expect_customer_be_updated(externalPerson, person, masterExternalId);
            return true;
        });
    }

}
