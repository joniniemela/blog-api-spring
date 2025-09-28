package com.jmnenterprises.blogapi.controller;

import com.jmnenterprises.blogapi.dto.LoginDTO;
import com.jmnenterprises.blogapi.dto.LoginResponse;
import com.jmnenterprises.blogapi.dto.RegisterDTO;
import com.jmnenterprises.blogapi.security.JWTUtil;
import com.jmnenterprises.blogapi.service.AuthService;
import com.jmnenterprises.blogapi.service.UserInfoConfigManager;
import com.jmnenterprises.blogapi.utils.ResponseHandler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JWTUtil jwtUtil;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserInfoConfigManager userInfoConfigManager;

    public AuthController(JWTUtil jwtUtil, AuthService authService,
                          AuthenticationManager authenticationManager,
                          UserInfoConfigManager userInfoConfigManager) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userInfoConfigManager = userInfoConfigManager;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            Object result = authService.register(registerDTO);
            return ResponseHandler.generateResponse("User registered successfully", HttpStatus.OK, result);
        } catch (RuntimeException e) {
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
            UserDetails userDetails = userInfoConfigManager.loadUserByUsername(loginDTO.getUsername());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            LoginResponse loginResponse = LoginResponse
                    .builder()
                    .accessToken(jwt)
                    .tokenType("Bearer")
                    .build();
            return ResponseHandler.generateResponse("User logged in successfully", HttpStatus.OK, loginResponse);
        }
        catch (Exception e)
        {
            return ResponseHandler.generateResponse("Incorrect username or password", HttpStatus.UNAUTHORIZED, null);
        }
    }
}