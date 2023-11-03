package com.durgesh.hibernate.IIII_mapping.onetoone;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Question {

    @Id
    @Column(name = "QSTN_ID")
    private int questionId;

    private String questionn;

    @OneToOne
    @JoinColumn(name = "ANS_ID")
    private Answer answer;

}
