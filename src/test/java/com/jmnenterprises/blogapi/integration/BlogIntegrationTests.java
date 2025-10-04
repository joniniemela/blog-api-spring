package com.jmnenterprises.blogapi.integration;


import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jmnenterprises.blogapi.dto.CreateBlogDTO;
import com.jmnenterprises.blogapi.dto.LoginDTO;
import com.jmnenterprises.blogapi.dto.RegisterDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("User can get all blogs")
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
    @DisplayName("User can get a blog by id")
    void userCanCreateAndGetBlogById() {

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
        Integer blogId = ctx.read("$.id");

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
    @DisplayName("User can create a blog")
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
    @DisplayName("User cannot create a blog without required fields")
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

    @Test
    @DisplayName("User can edit a blog user owns")
    void userCanEditABlogUserOwns() {
        // Create a blog first
        CreateBlogDTO newBlog = new CreateBlogDTO("Original Title", "Original Content");
        HttpEntity<CreateBlogDTO> createRequestEntity = new HttpEntity<>(newBlog, entity.getHeaders());

        ResponseEntity<String> createResponse = restTemplate.exchange(
                "/api/blog/create",
                HttpMethod.POST,
                createRequestEntity,
                String.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Extract the blog ID from the create response
        DocumentContext ctx = JsonPath.parse(createResponse.getBody());
        Integer blogId = ctx.read("$.id");
        assertThat(blogId).isNotNull();

        // Edit the blog
        CreateBlogDTO updatedBlog = new CreateBlogDTO("Updated Title", "Updated Content");
        HttpEntity<CreateBlogDTO> updateRequestEntity = new HttpEntity<>(updatedBlog, entity.getHeaders());

        ResponseEntity<String> updateResponse = restTemplate.exchange(
                "/api/blog/" + blogId,
                HttpMethod.PUT,
                updateRequestEntity,
                String.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotBlank();

        // Verify the blog was updated
        DocumentContext updateCtx = JsonPath.parse(updateResponse.getBody());
        String updatedTitle = updateCtx.read("$.title");
        String updatedContent = updateCtx.read("$.content");

        assertThat(updatedTitle).isEqualTo("Updated Title");
        assertThat(updatedContent).isEqualTo("Updated Content");

        // Verify by fetching the blog again
        ResponseEntity<String> getResponse = restTemplate.exchange(
                "/api/blog/" + blogId,
                HttpMethod.GET,
                entity,
                String.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext getCtx = JsonPath.parse(getResponse.getBody());
        assertThat(getCtx.read("$.title", String.class)).isEqualTo("Updated Title");
        assertThat(getCtx.read("$.content", String.class)).isEqualTo("Updated Content");
    }

    @Test
    @DisplayName("User cannot edit a blog user does not own")
    void userCannotEditABlogUserDoesNotOwn() {
        // First user creates a blog
        CreateBlogDTO newBlog = new CreateBlogDTO("Original Title", "Original Content");
        HttpEntity<CreateBlogDTO> createRequestEntity = new HttpEntity<>(newBlog, entity.getHeaders());

        ResponseEntity<String> createResponse = restTemplate.exchange(
                "/api/blog/create",
                HttpMethod.POST,
                createRequestEntity,
                String.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Extract the blog ID
        DocumentContext ctx = JsonPath.parse(createResponse.getBody());
        Integer blogId = ctx.read("$.id");
        assertThat(blogId).isNotNull();

        // Register and login as a different user
        RegisterDTO anotherUserRegister = new RegisterDTO("anotheruser", "Password123!", "another@example.com");
        restTemplate.postForEntity("/api/auth/register", anotherUserRegister, String.class);

        LoginDTO anotherUserLogin = new LoginDTO("anotheruser", "Password123!");
        ResponseEntity<String> anotherUserLoginResponse = restTemplate.postForEntity("/api/auth/login", anotherUserLogin, String.class);

        assertThat(anotherUserLoginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext anotherUserCtx = JsonPath.parse(anotherUserLoginResponse.getBody());
        String anotherUserToken = anotherUserCtx.read("$.data.accessToken");

        HttpHeaders anotherUserHeaders = new HttpHeaders();
        anotherUserHeaders.setBearerAuth(anotherUserToken);
        anotherUserHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

        // Try to edit the blog as the second user
        CreateBlogDTO updatedBlog = new CreateBlogDTO("Malicious Title", "Malicious Content");
        HttpEntity<CreateBlogDTO> updateRequestEntity = new HttpEntity<>(updatedBlog, anotherUserHeaders);

        ResponseEntity<String> updateResponse = restTemplate.exchange(
                "/api/blog/" + blogId,
                HttpMethod.PUT,
                updateRequestEntity,
                String.class
        );

        // Should return forbidden or not found
        assertThat(updateResponse.getStatusCode()).isIn(HttpStatus.FORBIDDEN, HttpStatus.NOT_FOUND, HttpStatus.UNAUTHORIZED);

        // Verify the blog was NOT updated by fetching it with the original user
        ResponseEntity<String> getResponse = restTemplate.exchange(
                "/api/blog/" + blogId,
                HttpMethod.GET,
                entity,
                String.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext getCtx = JsonPath.parse(getResponse.getBody());
        assertThat(getCtx.read("$.title", String.class)).isEqualTo("Original Title");
        assertThat(getCtx.read("$.content", String.class)).isEqualTo("Original Content");
    }

}
