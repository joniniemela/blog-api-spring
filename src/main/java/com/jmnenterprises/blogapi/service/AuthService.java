package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.RegisterDTO;
import com.jmnenterprises.blogapi.dto.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterDTO registerDTO);
}
