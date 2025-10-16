package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.request.LoginDTO;
import com.jmnenterprises.blogapi.dto.response.LoginResponse;
import com.jmnenterprises.blogapi.dto.request.RegisterDTO;
import com.jmnenterprises.blogapi.dto.response.RegisterResponse;
import com.jmnenterprises.blogapi.entity.User;
import com.jmnenterprises.blogapi.repository.AuthRepository;
import com.jmnenterprises.blogapi.security.JWTUtil;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final UserInfoConfigManager userInfoConfigManager;
    private final JWTUtil jwtUtil;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthServiceImpl(AuthRepository authRepository, ModelMapper modelMapper, UserInfoConfigManager userInfoConfigManager, AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authRepository = authRepository;
        this.modelMapper = modelMapper;
        this.userInfoConfigManager = userInfoConfigManager;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public RegisterResponse register(@NotNull RegisterDTO registerDTO) {
        if(authRepository.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        if (authRepository.existsByUsername(registerDTO.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        User user = modelMapper.map(registerDTO, User.class);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles(List.of("USER"));
        User save = authRepository.save(user);
        return modelMapper.map(save, RegisterResponse.class);
    }

    @Override
    public LoginResponse login(@NotNull LoginDTO loginDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );
        UserDetails userDetails = userInfoConfigManager.loadUserByUsername(loginDTO.getUsername());
        String jwt = jwtUtil.generateToken(userDetails.getUsername());
        return LoginResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .build();
    }

}
