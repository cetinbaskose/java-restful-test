package uk.co.huntersix.spring.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonDataService personDataService;

    @Test
    public void shouldReturnPersonFromService() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(Optional.of(new Person("Mary", "Smith")));
        this.mockMvc.perform(get("/person/smith/mary"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("firstName").value("Mary"))
                .andExpect(jsonPath("lastName").value("Smith"));
    }

    @Test
    public void shouldNotReturnPersonFromService() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(Optional.empty());
        this.mockMvc.perform(get("/person/baskose/cetin"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void shouldReturnPersonListBySurnameFromService() throws Exception {
        List<Person> personList = Arrays.asList(
                new Person("Mary", "Smith")
        );

        when(personDataService.findPersonBySurname(any())).thenReturn(personList);
        this.mockMvc.perform(get("/personBySurname/smith/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].firstName").value("Mary"))
                .andExpect(jsonPath("$[0].lastName").value("Smith"));
    }

    @Test
    public void shouldReturnNotFoundBySurnameFromServiceWhenPersonNotFound() throws Exception {
        when(personDataService.findPersonBySurname(any())).thenReturn(Collections.emptyList());

        this.mockMvc.perform(get("/personBySurname/baskose/"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }


    @Test
    public void shouldReturnStatusCreatedWhenNewPersonIsAdded() throws Exception {
        Person person = new Person("Cetin", "Baskose");
        ResponseEntity<Person> responseEntity = new ResponseEntity<Person>(person, HttpStatus.CREATED);
        when(personDataService.addPerson(any())).thenReturn(responseEntity);
        this.mockMvc.perform(post("/personAdd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("firstName").value("Cetin"))
                .andExpect(jsonPath("lastName").value("Baskose"));
    }


    @Test
    public void shouldReturnStatusConflictWhenNewPersonAlreadyExists() throws Exception {
        Person person = new Person("Mary", "Smith");
        ResponseEntity<Person> responseEntity = new ResponseEntity<Person>(HttpStatus.CONFLICT);
        when(personDataService.addPerson(any())).thenReturn(responseEntity);
        this.mockMvc.perform(post("/personAdd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").doesNotExist());
    }

}