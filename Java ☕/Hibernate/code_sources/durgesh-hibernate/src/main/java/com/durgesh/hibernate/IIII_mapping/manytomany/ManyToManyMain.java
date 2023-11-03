package com.durgesh.hibernate.IIII_mapping.manytomany;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class ManyToManyMain {

    public static void main(String[] args) {
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        // build `employees` and `projects`
        Employee employee1 = new Employee();
        Employee employee2 = new Employee();

        employee1.setName("ali");
        employee2.setName("taqi");

        Project project1 = new Project();
        Project project2 = new Project();

        project1.setName("DevVault");
        project2.setName("Reddit-Clone");

        List<Employee> employeeList = List.of(employee1, employee2);
        List<Project> projectList = List.of(project1, project2);

        employee1.setProjects(projectList);
        project2.setEmployees(employeeList);

        // save all the `projects` and `employees`
        session.save(employee1);
        session.save(employee2);
        session.save(project1);
        session.save(project2);

        // commit on the physical db
        transaction.commit();

        // close all the connections
        session.close();
        factory.close();
    }

}
