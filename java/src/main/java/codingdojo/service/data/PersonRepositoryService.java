package codingdojo.service.data;

import codingdojo.model.Person;
import codingdojo.repository.PersonRepository;

public class PersonRepositoryService extends CustomerRepositoryService<Person> {

    private final PersonRepository personRepository;

    public PersonRepositoryService(PersonRepository personRepository) {
        super(personRepository);
        this.personRepository = personRepository;
    }

    public CustomerMatches<Person> loadPerson(String externalId) {
        final CustomerMatches<Person> customerMatches;

        final Person matchByPersonalNumber = personRepository.findByExternalId(externalId);
        if (matchByPersonalNumber != null) {
            customerMatches = new CustomerMatches<>(matchByPersonalNumber);
        } else {
            customerMatches = new CustomerMatches<>();
        }

        return customerMatches;
    }

}
