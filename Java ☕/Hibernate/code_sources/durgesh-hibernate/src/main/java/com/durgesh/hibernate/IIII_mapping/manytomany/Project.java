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
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pId;

    private String name;

    @ManyToMany(mappedBy = "projects")
    private List<Employee> employees;

}
