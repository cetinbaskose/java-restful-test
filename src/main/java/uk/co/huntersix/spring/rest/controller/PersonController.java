package uk.co.huntersix.spring.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

import java.util.List;

@RestController
public class PersonController {
    private PersonDataService personDataService;

    public PersonController(@Autowired PersonDataService personDataService) {
        this.personDataService = personDataService;
    }

    @GetMapping("/person/{lastName}/{firstName}")
    public ResponseEntity<Person> person(@PathVariable(value = "lastName") String lastName, @PathVariable(value = "firstName") String firstName) {
        return personDataService.findPerson(lastName, firstName).map(resp -> ResponseEntity.ok().body(resp)).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/personBySurname/{lastName}")
    public ResponseEntity<List<Person>> personBySurname(
            @PathVariable(value = "lastName") String lastName) {
        List<Person> result = personDataService.findPersonBySurname(lastName);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/personAdd")
    public ResponseEntity<Person> addPerson(@RequestBody Person person) {
        return personDataService.addPerson(person);
    }

}