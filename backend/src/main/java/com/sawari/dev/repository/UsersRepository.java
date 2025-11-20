package com.sawari.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sawari.dev.model.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {
    boolean existsByUserName(String userName);  // Now this will work!
    boolean existsByEmail(String email);      
}