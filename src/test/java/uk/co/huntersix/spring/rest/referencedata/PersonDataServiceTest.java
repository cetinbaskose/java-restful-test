package uk.co.huntersix.spring.rest.referencedata;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import uk.co.huntersix.spring.rest.model.Person;

import java.util.Optional;

@SpringBootTest
public class PersonDataServiceTest {


    private PersonDataService personDataService = new PersonDataService();

    @Test
    public void shouldReturnPersonWhenPersonExists() {
        Optional<Person> result = personDataService.findPerson("Smith", "Mary");
        assert (result.isPresent());
        assert (result.get().getFirstName().equalsIgnoreCase("Mary"));
        assert (result.get().getLastName().equalsIgnoreCase("Smith"));
    }

    @Test
    public void shouldReturnOptionalEmptyWhenPersonNotExists() {
        Optional<Person> result = personDataService.findPerson("Baskose", "Cetin");
        assert (result.equals(Optional.empty()));
    }
}
