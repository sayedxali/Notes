package com.durgesh.hibernate.IIII_mapping.onetoone;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Answer {

    @Id
    @Column(name = "ANS_ID")
    private int answerId;

    private String answerr;

    @OneToOne(mappedBy = "answer", orphanRemoval = true)
    private Question question;

}
