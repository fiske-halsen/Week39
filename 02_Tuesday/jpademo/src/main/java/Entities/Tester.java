/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import DTO.PersonStyleDTO;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class Tester {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu");
        EntityManager em = emf.createEntityManager();

        Person p1 = new Person("Sumit", 1999);
        Person p2 = new Person("Jens", 1997);

        Address a1 = new Address("Store torv 1", 2323, "Nr. Snede");
        Address a2 = new Address("Valby Langgade", 2630, "Valby");

        p1.setAddres(a1);
        p2.setAddres(a2);

        Fee f1 = new Fee(100);
        Fee f2 = new Fee(200);
        Fee f3 = new Fee(300);

        p1.addFee(f1);
        p1.addFee(f3);
        p2.addFee(f2);

        SwimStyle s1 = new SwimStyle("Crawl");
        SwimStyle s2 = new SwimStyle("ButterFly");
        SwimStyle s3 = new SwimStyle("BreastStroke");

        p1.addSwimStyle(s1);
        p1.addSwimStyle(s3);
        p1.addSwimStyle(s2);

        em.getTransaction().begin();
        em.persist(p1);
        em.persist(p2);
        em.getTransaction().commit();

        em.getTransaction().begin();
        p1.removeSwimStyle(s3);
        em.getTransaction().commit();

        System.out.println("p1 " + p1.getP_id() + ", " + p1.getName());
        System.out.println("p2 " + p2.getP_id() + ", " + p2.getName());

        System.out.println("Sumits gade " + p1.getAddres().getStreet());
        System.out.println("Check om 2-vejs virker: " + a1.getPerson().getName());
        System.out.println("Hvem har betalt f2? Det har: " + f2.getPerson().getName());

        System.out.println("Hvad er der blevet betalt?");
        TypedQuery<Fee> q1 = em.createQuery("SELECT f FROM Fee f", Fee.class);
        List<Fee> fees = q1.getResultList();
        for (Fee f : fees) {
            System.out.println(f.getPerson().getName() + ": " + f.getAmount() + f.getPayDate());
        }
        
       TypedQuery<Person> q2 = em.createQuery("SELECT p FROM Person p", Person.class);
       List<Person> persons = q2.getResultList();
       
        for (Person p : persons) {
            System.out.println("Navn: " + p.getName());
            System.out.println("-Fees:");
            for (Fee f : p.getFees()) {
                System.out.println("--Beløb: " + f.getAmount() + ", " + f.getPayDate().toString());
            }
            System.out.println("---Styles:");
            for (SwimStyle ss: p.styles) {
                System.out.println("----Style: " + ss.getStyleName());
            }
            
            
        }
       
       
       
       
        System.out.println("JPQL Joins ");
        // Create JPQL with constructor
        Query q3 = em.createQuery("SELECT new DTO.PersonStyleDTO(p.name, p.year, s.styleName) FROM Person p JOIN p.styles s");
        List<PersonStyleDTO> personDetails = q3.getResultList();

        for (PersonStyleDTO ps : personDetails) {

            System.out.println("Navn: " + ps.getName() + " Year: " + ps.getYear() + " Style: " + ps.getSwimStyle());
        }
       
        

    }

}
