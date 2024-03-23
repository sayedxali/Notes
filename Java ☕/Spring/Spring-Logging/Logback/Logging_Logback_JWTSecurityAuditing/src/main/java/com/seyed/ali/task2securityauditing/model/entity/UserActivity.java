package com.seyed.ali.task2securityauditing.model.entity;

import com.seyed.ali.task2securityauditing.model.enums.ActivityType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@EntityListeners(AuditingEntityListener.class)
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userActivityId;

    @CreatedDate
    private Instant createdAt;

    private Long createdBy;
    @Column(length = 500)
    private String createdByName;
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

}
