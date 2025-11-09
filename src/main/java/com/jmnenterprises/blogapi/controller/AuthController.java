package com.jmnenterprises.blogapi.controller;

import com.jmnenterprises.blogapi.dto.request.LoginDTO;
import com.jmnenterprises.blogapi.dto.response.LoginResponse;
import com.jmnenterprises.blogapi.dto.request.RegisterDTO;
import com.jmnenterprises.blogapi.dto.response.RegisterResponse;
import com.jmnenterprises.blogapi.dto.response.UserResponse;
import com.jmnenterprises.blogapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            RegisterResponse registerResponse = authService.register(registerDTO);
            return ResponseEntity.ok(registerResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            LoginResponse loginResponse = authService.login(loginDTO);
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentUser(Authentication authentication) {
        try {
            String username = authentication.getName();
            UserResponse userResponse = authService.getCurrentUser(username);
            return ResponseEntity.ok(userResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}