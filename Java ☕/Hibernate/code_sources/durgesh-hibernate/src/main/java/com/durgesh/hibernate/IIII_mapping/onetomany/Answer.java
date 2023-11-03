package com.durgesh.hibernate.IIII_mapping.onetomany;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int answerId;

    private String answerr;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    public Answer(String s) {
        this.answerr = s;
    }
}
