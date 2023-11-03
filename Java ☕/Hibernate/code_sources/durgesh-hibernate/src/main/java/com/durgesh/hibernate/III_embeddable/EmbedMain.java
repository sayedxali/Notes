package com.durgesh.hibernate.III_embeddable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class EmbedMain {

    public static void main(String[] args) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        Student student1 = Student.builder()
                .id(1)
                .name("embed1")
                .city("embed1")
                .certificate(new Certificate("JAVA", "2yrs"))
                .build();

        Student student2 = Student.builder()
                .id(2)
                .name("embed2")
                .city("embed2")
                .certificate(new Certificate("SPRING_BOOT", "2yrs"))
                .build();

        session.save(student1);
        session.save(student2);

        transaction.commit();
        session.close();
        factory.close();
    }

}
