package com.durgesh.hibernate.IIII_mapping.onetoone;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class OneToOneMain {

    public static void main(String[] args) {
        SessionFactory factory = new Configuration().configure("hibernate.cfg.xml")
                .buildSessionFactory();
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        Answer answer1 = Answer.builder()
                .answerId(1)
                .answerr("A verbose programming language!")
                .build();
        Question question1 = Question.builder()
                .questionId(1)
                .questionn("What is Java?")
                .answer(answer1)
                .build();
        answer1.setQuestion(question1);

        Answer answer2 = Answer.builder()
                .answerId(2)
                .answerr("Intellij!")
                .build();
        Question question2 = Question.builder()
                .questionId(2)
                .questionn("What IDE is best for Java?")
                .answer(answer2)
                .build();
        answer2.setQuestion(question2);

        session.save(question1);
        session.save(answer1);

        session.save(question2);
        session.save(answer2);

        transaction.commit();

        // fetch
        Question question = session.get(Question.class, 1);
        System.out.println(question.getQuestionn());
        System.out.println(question.getAnswer().getAnswerr());

        session.close();
        factory.close();
    }

}
