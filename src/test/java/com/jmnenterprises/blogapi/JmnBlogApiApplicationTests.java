package com.jmnenterprises.blogapi;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jmnenterprises.blogapi.dto.LoginDTO;
import com.jmnenterprises.blogapi.dto.RegisterDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class JmnBlogApiApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void userRegistrationTest() {
        RegisterDTO registerRequest = new RegisterDTO("newuser", "Password123!", "newuser@example.com");

        ResponseEntity<String> registerResponse = restTemplate
                .postForEntity("/api/auth/register", registerRequest,  String.class);

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(registerResponse.getBody()).isNotNull();
    }

    @Test
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
}
