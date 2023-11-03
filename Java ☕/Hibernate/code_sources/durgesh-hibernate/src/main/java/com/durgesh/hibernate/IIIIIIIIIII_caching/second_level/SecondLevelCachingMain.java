package com.durgesh.hibernate.IIIIIIIIIII_caching.second_level;

import com.durgesh.hibernate.IIII_mapping.onetomany.Answer;
import com.durgesh.hibernate.IIII_mapping.onetomany.Question;
import com.durgesh.hibernate.III_embeddable.Certificate;
import com.durgesh.hibernate.III_embeddable.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class SecondLevelCachingMain {

    public static void main(String[] args) {
//        1. dependencies in pom.xml file
//        2. enable level2 cache in hibernate.cfg.xml file
//        3. enable cache for the entity (Student)
//========================================================

        SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        // first session
        Session session1 = factory.openSession();
        Transaction transaction = session1.beginTransaction();

        saveStudent(session1);
        saveQuestionAndAnswer(session1);

        Student student = session1.get(Student.class, 1);
        System.out.println(student);
        session1.close();

        // second session
        Session session2 = factory.openSession();
        Student isCachedStudent = session2.get(Student.class, 1);
        System.out.println(isCachedStudent);
        session2.close();

//=====================================================
        transaction.commit();
        factory.close();
    }

    private static void saveStudent(Session session) {
        for (int i = 0; i < 1000; i++) {
            Certificate certificate = new Certificate(i + "-ABFASF", i + 340 + "ASKFAL");
            session.save(new Student("ali" + (i + 1), (i + 1) + "qom", certificate));
        }
    }

    private static void saveQuestionAndAnswer(Session session) {
        Question question = new Question();

        Answer a1 = new Answer("Absolutely!!!");
        Answer a2 = new Answer("Yes....!!!");
        Answer a3 = new Answer("Meh..");
        Answer a4 = new Answer("Whatever..");
        List<Answer> answers = List.of(a1, a2, a3, a4);

        question.setQuestionn("Do you love Java?");
        question.setAnswer(answers);

        session.save(question);
    }

}
