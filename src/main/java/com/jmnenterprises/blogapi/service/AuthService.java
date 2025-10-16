package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.request.LoginDTO;
import com.jmnenterprises.blogapi.dto.response.LoginResponse;
import com.jmnenterprises.blogapi.dto.request.RegisterDTO;
import com.jmnenterprises.blogapi.dto.response.RegisterResponse;


public interface AuthService {
    RegisterResponse register(RegisterDTO registerDTO);
    LoginResponse login(LoginDTO loginDTO);
}
