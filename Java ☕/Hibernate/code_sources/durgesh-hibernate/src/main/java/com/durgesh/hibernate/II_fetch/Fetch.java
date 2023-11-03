package com.durgesh.hibernate.II_fetch;

import com.durgesh.hibernate.I_session.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Fetch {
    public static void main(String[] args) {
        SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();

        Session session = factory.openSession();

        // get student
        Student student = session.get(Student.class, 102); // returns null
        Student student1 = session.get(Student.class, 102); // returns null
        System.out.println(student);
        System.out.println(student1);

        Student student2 = session.load(Student.class, 102); // returns ObjectNotFoundException
        Student student3 = session.load(Student.class, 102); // returns ObjectNotFoundException
        System.out.println(student2.getCity());

        session.close();
        factory.close();
    }
}
