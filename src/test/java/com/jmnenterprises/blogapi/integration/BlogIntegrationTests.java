package com.jmnenterprises.blogapi.integration;


import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jmnenterprises.blogapi.dto.CreateBlogDTO;
import com.jmnenterprises.blogapi.dto.LoginDTO;
import com.jmnenterprises.blogapi.dto.RegisterDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BlogIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    HttpEntity<Void> entity;

    @BeforeEach
    void loginBeforeEach() {

        RegisterDTO registerRequest = new RegisterDTO("testuser", "Password123!", "testuser@example.com");
        restTemplate.postForEntity("/api/auth/register", registerRequest, String.class);


        LoginDTO loginRequest = new LoginDTO("testuser", "Password123!");
        ResponseEntity<String> loginResponse =
                restTemplate.postForEntity("/api/auth/login", loginRequest, String.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotBlank();

        DocumentContext ctx = JsonPath.parse(loginResponse.getBody());
        String tokenType = ctx.read("$.data.tokenType");
        assertThat(tokenType).isEqualTo("Bearer");

        String jwtToken = ctx.read("$.data.accessToken");
        assertThat(jwtToken).isNotBlank();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);

        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        entity = new HttpEntity<>(headers);
    }

    @Test
    void userCanGetAllBlogs() {

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/blog",
                HttpMethod.GET,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotBlank();
    }

    @Test
    void userCanCreateAndGetBlogById() {
        // Create a new blog
        CreateBlogDTO newBlog = new CreateBlogDTO("TestTitle", "TestContent");
        HttpEntity<CreateBlogDTO> requestEntity = new HttpEntity<>(newBlog, entity.getHeaders());

        ResponseEntity<String> createResponse = restTemplate.exchange(
                "/api/blog/create",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        DocumentContext ctx = JsonPath.parse(createResponse.getBody());
        Integer blogId = ctx.read("$.data.id");

        ResponseEntity<String> getResponse = restTemplate.exchange(
                "/api/blog/" + blogId,
                HttpMethod.GET,
                entity,
                String.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).contains("TestTitle");
    }


    @Test
    void userCanCreateABlog() {

        CreateBlogDTO newBlog = new CreateBlogDTO("TervetuloaTesti", "Tämä on blogin sisältöteksti.");
        HttpEntity<CreateBlogDTO> requestEntity = new HttpEntity<>(newBlog, entity.getHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/blog/create",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void userCannotCreateBlogWithoutRequiredFields() {
        CreateBlogDTO newBlog1 = new CreateBlogDTO(null, "Tämä on blogin sisältöteksti.");
        HttpEntity<CreateBlogDTO> requestEntity1 = new HttpEntity<>(newBlog1, entity.getHeaders());

        ResponseEntity<String> response1 = restTemplate.exchange(
                "/api/blog/create",
                HttpMethod.POST,
                requestEntity1,
                String.class
        );

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        CreateBlogDTO newBlog2 = new CreateBlogDTO("TervetuloaTesti", null);
        HttpEntity<CreateBlogDTO> requestEntity2 = new HttpEntity<>(newBlog2, entity.getHeaders());

        ResponseEntity<String> response2 = restTemplate.exchange(
                "/api/blog/create",
                HttpMethod.POST,
                requestEntity2,
                String.class
        );

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    }

}
