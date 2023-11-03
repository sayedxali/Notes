package com.durgesh.hibernate.IIIIIII_hql;

import com.durgesh.hibernate.IIII_mapping.onetomany.Answer;
import com.durgesh.hibernate.IIII_mapping.onetomany.Question;
import com.durgesh.hibernate.I_session.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.Arrays;
import java.util.List;

public class HQLMain {

    public static void main(String[] args) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        session.save(new Student(1, "ali", "qom"));
        session.save(new Student(2, "mehdi", "tehran"));
        session.save(new Student(3, "reza", "mumbai"));

        // HQL : select
        String query = "FROM Student as std WHERE std.city=:cty AND std.name=:nme";
        Query<Student> q1 = session.createQuery(query, Student.class);
        q1.setParameter("cty", "tehran");
        q1.setParameter("nme", "mehdi");

        // single data - unique
        // multiple data - list
        List<Student> list = q1.list();

        for (Student std : list) {
            System.out.println(std.getName());
        }

        System.out.println("---------------------------------------");
        // HQL : delete
        Query q2 = session.createQuery("DELETE FROM Student as std WHERE std.city=:cty");
        q2.setParameter("cty", "tehran");
        int r = q2.executeUpdate();
        System.out.println("Deleted: " + r);

        System.out.println("---------------------------------------");
        // HQL : update
        Query q3 = session.createQuery("UPDATE Student SET city=:cty WHERE name=:nme");
        q3.setParameter("cty", "tehran");
        q3.setParameter("nme", "mehdi laghaee");
        int r3 = q3.executeUpdate();
        System.out.println("updated: " + r3 + " - ");


        System.out.println("---------------------------------------");
        // HQL : join
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

        String joinQuery = "SELECT q.questionId, q.questionn, a.answerr FROM Question AS q INNER JOIN q.answer AS a";
        Query q4 = session.createQuery(joinQuery);
        List<Object[]> q4ResultList = q4.getResultList();
        for (Object[] arr : q4ResultList) {
            System.out.println(Arrays.toString(arr));
        }


        transaction.commit();
        session.close();
        factory.close();
    }

}
