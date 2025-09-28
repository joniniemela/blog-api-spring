package com.jmnenterprises.blogapi.repository;

import com.jmnenterprises.blogapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}

