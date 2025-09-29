package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.RegisterDTO;
import com.jmnenterprises.blogapi.dto.RegisterResponse;
import com.jmnenterprises.blogapi.entity.User;
import com.jmnenterprises.blogapi.repository.AuthRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks AuthServiceImpl authService;

    @Test
    void shouldRegisterUser() {
        RegisterDTO registerRequest = new RegisterDTO("testloginuser123", "Password123!", "test@example.com");
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        RegisterResponse response = new RegisterResponse();
        response.setUsername(registerRequest.getUsername());

        when(modelMapper.map(registerRequest, User.class)).thenReturn(user);
        when(authRepository.save(user)).thenReturn(user);
        when(authRepository.findByUsername(registerRequest.getUsername())).thenReturn(user);
        when(modelMapper.map(user, RegisterResponse.class)).thenReturn(response);

        RegisterResponse result = authService.register(registerRequest);

        User repositoryResult = authRepository.findByUsername(registerRequest.getUsername());

        assertEquals(repositoryResult.getUsername(), result.getUsername());
    }

}
