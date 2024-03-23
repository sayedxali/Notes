package com.seyed.ali.task2securityauditing.repository;

import com.seyed.ali.task2securityauditing.model.entity.HogwartsUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<HogwartsUser, Integer> {

    Optional<HogwartsUser> findByUsername(String username);

}