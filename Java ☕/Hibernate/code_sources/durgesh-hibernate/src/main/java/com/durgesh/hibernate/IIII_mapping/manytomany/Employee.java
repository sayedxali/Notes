package com.durgesh.hibernate.IIII_mapping.manytomany;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eId;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "EMPLOYEE_PROJECT",
            joinColumns = {@JoinColumn(name = "EMP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "PROJECT_ID")}
    )
    private List<Project> projects;

}
