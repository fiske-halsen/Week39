package rest;

import DTO.PersonDTO;
import entities.Person;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
//Uncomment the line below, to temporarily disable this test

@Disabled
public class PersonResourceTest {
    
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Person p1, p2;
    
    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    
    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }
    
    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        
        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }
    
    @AfterAll
    public static void closeTestServer() {
        //System.in.read();
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        p1 = new Person("Jon", "Bertelsen", "27394632");
        p2 = new Person("Phillip", "Andersen", "42913009");
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNativeQuery("alter table PERSON AUTO_INCREMENT = 1").executeUpdate();
            em.persist(p1);
            em.persist(p2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/xxx").then().statusCode(200);
    }

    //This test assumes the database contains two rows
    @Test
    public void testDummyMsg() throws Exception {
        given()
                .contentType("application/json")
                .get("/xxx/").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("msg", equalTo("Hello World"));
    }
    
    @Test
    public void testPersonId() throws Exception {
        given()
                .contentType("application/json")
                .get("/xxx/id/{id}", p1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("fName", is("Jon"))
                .body("lName", is("Bertelsen"))
                .body("phone", is("27394632"));
    }
    
    @Test
    public void testGetAllPersons() throws Exception {
        
        List<PersonDTO> personsDTOs;
        
        personsDTOs = given()
                .contentType("application/json")
                .when()
                .get("/xxx/all")
                .then()
                .extract().body().jsonPath().getList("all", PersonDTO.class);
        
        PersonDTO p1DTO = new PersonDTO(p1);
        PersonDTO p2DTO = new PersonDTO(p2);
        
        assertThat(personsDTOs, containsInAnyOrder(p1DTO, p2DTO));
        
    }
    
    @Test
    public void testDeletePerson() throws Exception {
        
        PersonDTO person = new PersonDTO(p1);
        
        given()
                .contentType("application/json")
                .delete("/xxx/delete/" + person.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
        
        List<PersonDTO> personsDTOs;
        
        personsDTOs = given()
                .contentType("application/json")
                .when()
                .get("/xxx/all")
                .then()
                .extract().body().jsonPath().getList("all", PersonDTO.class);
        
        PersonDTO p2DTO = new PersonDTO(p2);
        
        assertThat(personsDTOs, containsInAnyOrder(p2DTO));
    }
    
    @Test
    public void testAddPerson() throws Exception {
        
        given()
                .contentType(ContentType.JSON)
                .body(new PersonDTO("Bamse", "Olsen", "2132131"))
                .when()
                .post("xxx/insertPerson")
                .then()
                .body("fName", equalTo("Bamse"))
                .body("lName", equalTo("Olsen"))
                .body("phone", equalTo("2132131"));
        
    }
    
    @Test
    public void updatePerson() throws Exception {
        
        PersonDTO person = new PersonDTO(p1);
        
        person.setPhone("42913009");
        
        given()
                .contentType(ContentType.JSON)
                .body(person)
                .when()
                .put("xxx/editperson/" + person.getId())
                .then()
                .body("fName", equalTo("Jon"))
                .body("lName", equalTo("Bertelsen"))
                .body("phone", equalTo("42913009"))
                .body("id", equalTo((int) person.getId()));
        
    }
    
    @Test
    public void updatePersonErrorTesting() {
        long fakeId = 3;
        Person p1 = new Person("Ole", "Hansen", "25545122");
        p1.setId(fakeId);
        PersonDTO personDTO = new PersonDTO(p1);
        personDTO.setId(fakeId);
        personDTO.setPhone("42913009");
        
        
        given()
                .contentType(ContentType.JSON)
                .body(personDTO)
                .when()
                .put("xxx/editperson/" + personDTO.getId())
                .then()
                .body("code", equalTo(404))
                .body("message", equalTo("Could not edit the person with the provided id"));
        
    }
    
    @Test
    public void deletePersonErrorTesting(){
        long fakeId = 3;
        Person person = new Person("Ole", "Hansen", "823123213");
        person.setId(fakeId);
        PersonDTO personDTO = new PersonDTO(person);
        
        given()
                .contentType("application/json")
                .delete("/xxx/delete/" + personDTO.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode());
        
        
    }
    
    
    
    
    
    
    
    
}
