package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.RegisterDTO;
import com.jmnenterprises.blogapi.dto.RegisterResponse;
import com.jmnenterprises.blogapi.entity.User;
import com.jmnenterprises.blogapi.repository.AuthRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final ModelMapper modelMapper;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthServiceImpl(AuthRepository authRepository, ModelMapper modelMapper) {
        this.authRepository = authRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RegisterResponse register(RegisterDTO registerDTO) {
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
}
