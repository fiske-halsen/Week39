package facades;

import DTO.PersonDTO;
import DTO.PersonsDTO;
import exceptions.PersonNotFoundException;

public interface IPersonFacade {

    public PersonDTO addPerson(String fName, String lName, String phone, String city, String street, int zip);
    public PersonDTO deletePerson(long id) throws PersonNotFoundException;
    public PersonDTO getPerson(long id) throws PersonNotFoundException;
    public PersonsDTO getAllPersons();
    public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException;
    

}
