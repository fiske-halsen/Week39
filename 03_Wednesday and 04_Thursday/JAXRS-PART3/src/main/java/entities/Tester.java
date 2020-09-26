package entities;

import DTO.PersonDTO;
import exceptions.PersonNotFoundException;
import facades.PersonFacade;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class Tester {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu");
        EntityManager em = emf.createEntityManager();

        Person p1 = new Person("Sumit", "Hansen", "21321321");
        Person p2 = new Person("Jens", "Olsen", "21321321");

        Address a1 = new Address("Store torv 1", 2323, "Nr. Snede");
        Address a2 = new Address("Valby Langgade", 2630, "Valby");

        p1.setAddress(a1);
        p2.setAddress(a2);

        em.getTransaction().begin();
        em.persist(p1);
        em.persist(p2);
        em.getTransaction().commit();

        System.out.println("p1 " + p1.getId() + ", " + p1.getFirstName());
        System.out.println("p2 " + p2.getId() + ", " + p2.getFirstName());

        System.out.println("Sumits gade " + p1.getAddress());
         System.out.println("Check om 2-vejs virker: " + a1.getPerson().getFirstName());

    }

}
