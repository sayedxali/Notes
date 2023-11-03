package com.durgesh.hibernate.IIIIIIII_pagination;

import com.durgesh.hibernate.III_embeddable.Certificate;
import com.durgesh.hibernate.III_embeddable.Student;
import com.durgesh.hibernate.IIII_mapping.onetomany.Answer;
import com.durgesh.hibernate.IIII_mapping.onetomany.Question;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

public class VIII_pagination {

    public static void main(String[] args) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        saveStudent(session);
        saveQuestionAndAnswer(session);


        Query<Student> query = session.createQuery("FROM Student", Student.class);

        // implement pagination using hibernate
        query.setFirstResult(10);
        query.setMaxResults(5); // max page size

        List<Student> list = query.list();
        list.forEach(student ->
                System.out.println(student.getId() + " : " + student.getName() + " : " + student.getCity())
        );


        transaction.commit();
        session.close();
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
        Answer a1 = new Answer(1, "a1", question);
        Answer a2 = new Answer(2, "a2", question);
        Answer a3 = new Answer(3, "a3", question);
        Answer a4 = new Answer(4, "a4", question);
        List<Answer> answers = List.of(a1, a2, a3, a4);

        question.setQuestionn("q1");
        question.setAnswer(answers);

        session.save(a1);
        session.save(a2);
        session.save(a3);
        session.save(a4);
        session.save(question);
    }


}
