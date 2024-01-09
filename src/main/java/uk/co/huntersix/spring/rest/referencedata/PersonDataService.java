package uk.co.huntersix.spring.rest.referencedata;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.co.huntersix.spring.rest.model.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonDataService {
    private static ArrayList<Person> PERSON_DATA = new ArrayList<>(Arrays.asList(
            new Person("Mary", "Smith"),
            new Person("Brian", "Archer"),
            new Person("Collin", "Brown"),
            new Person("Carolina", "Brown")
    ));

    public Optional<Person> findPerson(String lastName, String firstName) {
        return PERSON_DATA.stream()
                .filter(p -> p.getFirstName().equalsIgnoreCase(firstName)
                        && p.getLastName().equalsIgnoreCase(lastName))
                .findFirst();
    }

    public List<Person> findPersonBySurname(String lastName) {
        return PERSON_DATA.stream().filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toList());
    }

    public ResponseEntity<Person> addPerson(Person person) {

        Optional<Person> existingPerson= PERSON_DATA.stream().filter(
                        p -> p.getFirstName().equalsIgnoreCase(person.getFirstName()) && p.getLastName()
                                .equalsIgnoreCase(person.getLastName())).findAny();

        if (!existingPerson.isPresent()) {
            PERSON_DATA.add(person);
            return new ResponseEntity<>(person, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        }
    }


}
