package com.example.demoToDo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demoToDo.model.User;

public interface UserRepository extends JpaRepository<User,Long>{
    Optional<User>findByEmail(String email);
    
}
