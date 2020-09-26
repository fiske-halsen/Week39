package rest;

import DTO.PersonDTO;
import DTO.PersonsDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.PersonNotFoundException;
import utils.EMF_Creator;
import facades.PersonFacade;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//Todo Remove or change relevant parts before ACTUAL use
@Path("xxx")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    //An alternative way to get the EntityManagerFactory, whithout having to type the details all over the code
    //EMF = EMF_Creator.createEntityManagerFactory(DbSelector.DEV, Strategy.CREATE);
    private static final PersonFacade FACADE = PersonFacade.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String msg() {
        return "{\"msg\":\"Hello World\"}";
    }

    @GET
    @Path("/id/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String getPersonById(@PathParam("id") int id) throws PersonNotFoundException {
        
        PersonDTO personDTO = FACADE.getPerson(id);
        
        return GSON.toJson(personDTO);
    }

    @GET
    @Path("all")
    @Produces({MediaType.APPLICATION_JSON})
    public String allPersons() {

        PersonsDTO personsDTO = FACADE.getAllPersons();
        
        return GSON.toJson(personsDTO);
    }

    @POST
    @Path("insertPerson")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertPerson(String person) {

        PersonDTO p1 = GSON.fromJson(person, PersonDTO.class);
        p1 = FACADE.addPerson(p1.getfName(), p1.getlName(), p1.getPhone());
        return Response.ok(p1).build();

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("editperson/{id}")
    public Response editPerson(@PathParam("id") long id, String person) throws PersonNotFoundException {
        PersonDTO p1 = GSON.fromJson(person, PersonDTO.class);
        p1.setId(id);
        PersonDTO personEdit = FACADE.editPerson(p1);
        return Response.ok(personEdit).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("delete/{id}")
    public String deletePerson(@PathParam("id") long id) throws PersonNotFoundException {
        // Fetch and Remove the car using the provided id 
        PersonDTO personDeleted = FACADE.deletePerson(id);

        // Come up with a “sensible” response for the request  
        return "{\"status\": \"removed\"}";
    }

}
