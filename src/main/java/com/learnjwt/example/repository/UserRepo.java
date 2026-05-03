package com.learnjwt.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learnjwt.example.entity.AppUser;

public interface UserRepo extends JpaRepository<AppUser, Long>{
    boolean existsByUsername(String username);

    Optional<AppUser> findByUsername(String username);
}
