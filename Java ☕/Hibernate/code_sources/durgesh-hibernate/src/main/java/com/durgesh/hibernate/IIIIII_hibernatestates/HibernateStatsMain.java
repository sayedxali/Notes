package com.durgesh.hibernate.IIIIII_hibernatestates;

import com.durgesh.hibernate.I_session.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class HibernateStatsMain {

    public static void main(String[] args) {
        // Transient

        // Persistent

        // Detached

        // Removed

        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        // create student object
        Student student = new Student(1, "hello", "hi");
        // student =: transient state

        session.save(student);
//         or
//        session.persist(student);
        // student =: persistent state
        student.setCity("DELHI");

        transaction.commit();
        session.close();

        student.setName("SyedAli");
        System.out.println(student);
        // student =: detached state

        factory.close();
    }

}
