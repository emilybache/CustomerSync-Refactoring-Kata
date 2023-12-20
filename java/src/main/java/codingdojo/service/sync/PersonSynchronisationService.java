package codingdojo.service.sync;

import codingdojo.exception.ConflictException;
import codingdojo.model.CustomerType;
import codingdojo.model.ExternalPerson;
import codingdojo.model.Person;
import codingdojo.repository.PersonRepository;
import codingdojo.repository.ShoppingListRepository;
import codingdojo.service.data.CustomerMatches;
import codingdojo.service.data.PersonRepositoryService;

public class PersonSynchronisationService {

    private final PersonRepositoryService personRepositoryService;
    private final BaseSynchronisationService<ExternalPerson, Person> personSynchronisationService;

    public PersonSynchronisationService(PersonRepository personRepository, ShoppingListRepository shoppingListRepository) {
        personRepositoryService = new PersonRepositoryService(personRepository);
        personSynchronisationService = new BaseSynchronisationService<>(
                shoppingListRepository,
                personRepositoryService,
                Person::new,
                new PersonFieldsSynchronisationService()
        );
    }

    public SyncResult synchronise(ExternalPerson externalPerson) {
        final CustomerMatches<Person> customerMatches = personRepositoryService.loadPerson(externalPerson.getExternalId());
        validateCustomerType(externalPerson.getExternalId(), customerMatches);
        return personSynchronisationService.synchronise(externalPerson, customerMatches);
    }

    private static void validateCustomerType(String externalId, CustomerMatches<Person> customerMatches) {
        if (customerMatches.getCustomer() != null) {
            if (CustomerType.PERSON != customerMatches.getCustomer().getCustomerType()) {
                throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a person");
            }
        }
    }

}
