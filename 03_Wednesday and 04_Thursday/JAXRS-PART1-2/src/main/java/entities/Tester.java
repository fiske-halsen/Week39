package entities;

import DTO.PersonDTO;
import facades.PersonFacade;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class Tester {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu");
        EntityManager em = emf.createEntityManager();

        Person p1 = new Person("Phillip", "Andersen", "26352151");

        em.getTransaction().begin();
        em.persist(p1);
        em.getTransaction().commit();

      

    }

}
