package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.request.LoginDTO;
import com.jmnenterprises.blogapi.dto.response.LoginResponse;
import com.jmnenterprises.blogapi.dto.request.RegisterDTO;
import com.jmnenterprises.blogapi.dto.response.RegisterResponse;
import com.jmnenterprises.blogapi.entity.User;
import com.jmnenterprises.blogapi.repository.AuthRepository;
import com.jmnenterprises.blogapi.security.JWTUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Service Tests")
public class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserInfoConfigManager userInfoConfigManager;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    AuthServiceImpl authService;

    @Nested
    @DisplayName("User authentication")
    class UserAuthenticationTests {
        @DisplayName("User can register")
        @Test
        void userCanBeRegistered() {
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

        @DisplayName("User can login and receive jwt token")
        @Test
        void userCanLoginAndReceiveAccessToken() {
            LoginDTO loginRequest = new LoginDTO("testloginuser123", "Password123!");

            when(authenticationManager.authenticate(any())).thenReturn(mock(org.springframework.security.core.Authentication.class));
            UserDetails userDetails = mock(UserDetails.class);
            when(userInfoConfigManager.loadUserByUsername(loginRequest.getUsername())).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn(loginRequest.getUsername());
            when(jwtUtil.generateToken(loginRequest.getUsername())).thenReturn("mocked-access-token");

            LoginResponse result = authService.login(loginRequest);

            assertEquals("mocked-access-token", result.getAccessToken());
            assertEquals("Bearer", result.getTokenType());
        }

    }


}
