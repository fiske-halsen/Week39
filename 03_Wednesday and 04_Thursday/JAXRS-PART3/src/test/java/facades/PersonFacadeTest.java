package facades;

import DTO.PersonDTO;
import DTO.PersonsDTO;
import utils.EMF_Creator;
import entities.Person;
import exceptions.PersonNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

//Uncomment the line below, to temporarily disable this test
@Disabled
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    Person p1;
    Person p2;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = PersonFacade.getFacadeExample(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the script below to use YOUR OWN entity class
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

    @AfterEach
    public void tearDown() {

    }

    @Test
    public void testGetPersonById() throws PersonNotFoundException {
        PersonDTO person = facade.getPerson(p1.getId());
        String expectedFirstName = "Jon";
        assertEquals(expectedFirstName, person.getfName(), "Expects Jon");
    }

    @Test
    public void testGetAllPersons() {
        PersonsDTO persons = facade.getAllPersons();
        assertEquals(2, persons.getAll().size(), "Expects the size of two");
        assertThat(persons.getAll(), everyItem(hasProperty("fName")));
    }

    @Test
    public void testDeletePerson() throws PersonNotFoundException {
        PersonDTO personDeleted = facade.deletePerson(p1.getId());
        assertEquals("Jon", personDeleted.getfName());

    }

    @Test
    public void testAddPerson() {
     //   PersonDTO personDTO = facade.addPerson("Hans", "Christensen", "2323214141");
      //  PersonsDTO persons = facade.getAllPersons();
        //assertThat(persons.getAll(), hasSize(3));

    }

    @Test
    public void editPerson() throws PersonNotFoundException {

        PersonDTO personDTO = new PersonDTO(p1);
        personDTO.setfName("Christian");
        PersonDTO personEdited = facade.editPerson(personDTO);
        assertEquals("Christian", personEdited.getfName());

    }

    //Negative tests
    @Test
    public void editPersonError() throws PersonNotFoundException {
        Person personFake = new Person("Fake", "Fake", "Fake");
        long id = 5;
        try {
            personFake.setId(id);
            PersonDTO personDTO = new PersonDTO(personFake);
            personDTO.setfName("Christian");
            PersonDTO personEdited = facade.editPerson(personDTO);
            assert false;
        } catch (PersonNotFoundException e) {
            assert true;
        }
    }

    @Test
    public void testGetPersonByIdError() throws PersonNotFoundException {
        long fakeId = 121;
        try {
            PersonDTO person = facade.getPerson(fakeId);
        } catch (PersonNotFoundException ex) {
            assertThat(ex.getMessage(), is("No person with provided id found"));
        }
    }
    
    
    
    
    
    
    

}
