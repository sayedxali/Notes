package com.durgesh.hibernate.I_session;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        System.out.println("Project started>>> ...");

        // this is a connection -> also we can extract sessions which is a thread-safe
        // and there should only be one session; it helps data saving
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
        //===============================//
        // save a student ID
        //===============================//
        Student student = new Student(102, "SEYED", "QOM");
        System.out.println("=> " + student);

        FileInputStream fileInputStream = new FileInputStream("src/main/resources/img/genshin_noelle.jpg");
        byte[] data = new byte[fileInputStream.available()];
        fileInputStream.read(data);

        Address address = new Address();
        address.setStreet("st1");
        address.setCity("city1");
        address.setImage(data);

        // open a session
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        session.save(student);
        session.save(address);

        transaction.commit();
        session.close();
    }

}
