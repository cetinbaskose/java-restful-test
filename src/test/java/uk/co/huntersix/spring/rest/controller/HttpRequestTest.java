package uk.co.huntersix.spring.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.huntersix.spring.rest.model.Person;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldReturnPersonDetails() {
        assertThat(
                this.restTemplate.getForObject(
                        "http://localhost:" + port + "/person/smith/mary",
                        String.class
                )
        ).contains("Mary");
    }

    @Test
    public void shouldReturnNotFoundPersonBySurname() {
        assertThat(
                this.restTemplate.getForEntity(
                        "http://localhost:" + port + "/personBySurname/NoOne",
                        String.class
                ).getStatusCodeValue()
        ).isEqualTo(404);
    }

    @Test
    public void shouldReturnPersonBySurname() {
        assertThat(
                this.restTemplate.getForObject(
                        "http://localhost:" + port + "/personBySurname/Archer",
                        String.class
                )
        ).contains("Brian");
    }

    @Test
    public void shouldReturnMultiplePersonBySurname() {
        assertThat(
                this.restTemplate.getForObject(
                        "http://localhost:" + port + "/personBySurname/Brown",
                        String.class
                )
        ).contains("Collin", "Carolina");
    }


    @Test
    public void shouldAddPerson() throws JSONException, JsonProcessingException {
        Person person = new Person("John", "Depp");
        person.setId(null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(person), headers);

        ResponseEntity<Person> responseEntity = this.restTemplate.postForEntity("http://localhost:" + port + "/personAdd", requestEntity, Person.class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(201);
        assertThat(responseEntity.getBody().getFirstName()).contains("John");
        assertThat(responseEntity.getBody().getLastName()).contains("Depp");
    }

    @Test
    public void shouldReturnConflictAddPersonWhenPersonAlreadyExists() throws JSONException, JsonProcessingException {
        Person person = new Person("Mary", "Smith");
        person.setId(null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(person), headers);

        ResponseEntity<Person> responseEntity = this.restTemplate.postForEntity("http://localhost:" + port + "/personAdd", requestEntity, Person.class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(409);
        assertThat(responseEntity.hasBody()).isFalse();
    }


}