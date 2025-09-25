package com.jmnenterprises.blogapi.repository;

import com.jmnenterprises.blogapi.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    boolean existsByUsername(String username);
    User findByEmail(String email);
    boolean existsByEmail(String email);


}

