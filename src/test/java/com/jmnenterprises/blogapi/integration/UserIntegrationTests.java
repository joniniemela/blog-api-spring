package com.jmnenterprises.blogapi.integration;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jmnenterprises.blogapi.dto.LoginDTO;
import com.jmnenterprises.blogapi.dto.RegisterDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    @DisplayName("User can register")
    void userRegistrationTest() {
        RegisterDTO registerRequest = new RegisterDTO("newuser", "Password123!", "newuser@example.com");

        ResponseEntity<String> registerResponse = restTemplate
                .postForEntity("/api/auth/register", registerRequest,  String.class);

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(registerResponse.getBody()).isNotNull();
        assertThat(registerResponse.getBody()).contains("newuser");
    }

    @Test
    @DisplayName("User can login")
    void userLoginTest() {

        RegisterDTO registerRequest = new RegisterDTO("testloginuser", "Password123!", "test@example.com");
        ResponseEntity<String> registerResponse = restTemplate
                .postForEntity("/api/auth/register", registerRequest, String.class);
        Assertions.assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        LoginDTO loginRequest = new LoginDTO("testloginuser", "Password123!");
        ResponseEntity<String> loginResponse = restTemplate
                .postForEntity("/api/auth/login", loginRequest,  String.class);

        Assertions.assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(loginResponse.getBody()).isNotNull();


        DocumentContext documentContext = JsonPath.parse(loginResponse.getBody());
        String tokenType = documentContext.read("$.data.tokenType");
        Assertions.assertThat(tokenType).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("User cannot login with wrong credentials")
    void cannotLoginWithWrongCredentials() {

        RegisterDTO registerRequest = new RegisterDTO("testloginuser123", "Password123!", "test@example.com");
        ResponseEntity<String> registerResponse = restTemplate
                .postForEntity("/api/auth/register", registerRequest, String.class);
        Assertions.assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        LoginDTO loginRequest1 = new LoginDTO("testloginuser123", "WrongPassword!");
        ResponseEntity<String> loginResponse1 = restTemplate
                .postForEntity("/api/auth/login", loginRequest1,  String.class);
        Assertions.assertThat(loginResponse1.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        LoginDTO loginRequest2 = new LoginDTO("testlogin", "Password123!");
        ResponseEntity<String> loginResponse2 = restTemplate
                .postForEntity("/api/auth/login", loginRequest2,  String.class);
        Assertions.assertThat(loginResponse2.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        LoginDTO loginRequest3 = new LoginDTO(null, null);
        ResponseEntity<String> loginResponse3 = restTemplate
                .postForEntity("/api/auth/login", loginRequest3,  String.class);
        Assertions.assertThat(loginResponse3.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    }

}
