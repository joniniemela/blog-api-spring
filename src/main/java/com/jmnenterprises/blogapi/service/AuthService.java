package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.RegisterDTO;
import com.jmnenterprises.blogapi.dto.RegisterResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    RegisterResponse register(RegisterDTO registerDTO);
}
