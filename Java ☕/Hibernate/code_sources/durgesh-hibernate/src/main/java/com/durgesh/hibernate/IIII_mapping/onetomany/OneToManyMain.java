package com.durgesh.hibernate.IIII_mapping.onetomany;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class OneToManyMain {

    public static void main(String[] args) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        // build `question` and `answers`
        Question question = new Question();
        question.setQuestionn("What is Java?");

        Answer answer = new Answer();
        answer.setAnswerr("A verbose p language :)");
        answer.setQuestion(question);

        Answer answer1 = new Answer();
        answer1.setAnswerr("P language for back-end!");
        answer1.setQuestion(question);

        Answer answer2 = new Answer();
        answer2.setAnswerr("P language for android!");
        answer2.setQuestion(question);

        List<Answer> answers = List.of(answer, answer1, answer2);
        question.setAnswer(answers);

        // save all the `answers` and `question`
        session.save(question);
        session.save(answer);
        session.save(answer1);
        session.save(answer2);

        // commit on the physical db
        transaction.commit();

        // close all the connections
        session.close();
        factory.close();
    }

}
