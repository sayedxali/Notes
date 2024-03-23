package com.seyed.ali.task2securityauditing.repository;

import com.seyed.ali.task2securityauditing.model.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
}