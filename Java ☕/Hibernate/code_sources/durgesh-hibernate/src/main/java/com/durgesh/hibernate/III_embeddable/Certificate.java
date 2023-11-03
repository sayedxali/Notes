package com.durgesh.hibernate.III_embeddable;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor

@Embeddable
public class Certificate {

    private String course;
    private String duration;

}
