package com.jmnenterprises.blogapi.integration;


import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jmnenterprises.blogapi.dto.request.CreateBlogDTO;
import com.jmnenterprises.blogapi.dto.request.LoginDTO;
import com.jmnenterprises.blogapi.dto.request.RegisterDTO;
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

    private HttpEntity<Void> entity;

    private static final String TEST_TITLE = "Original Title";
    private static final String TEST_CONTENT = "Original Content";
    private static final String UPDATED_TITLE = "Updated Title";
    private static final String UPDATED_CONTENT = "Updated Content";

    @BeforeEach
    void loginBeforeEach() {
        entity = registerAndLoginUser("testuser", "testuser@example.com");
    }

    @Test
    @DisplayName("User can get all blogs")
    void userCanGetAllBlogs() {
        ResponseEntity<String> response = getBlogList();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotBlank();
    }

    @Test
    @DisplayName("User can get a blog by id")
    void userCanCreateAndGetBlogById() {
        Integer blogId = createBlogAndGetId(createBlogDTO("TestTitle", "TestContent"));

        ResponseEntity<String> response = getBlogById(blogId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("TestTitle");
    }

    @Test
    @DisplayName("User can create a blog")
    void userCanCreateABlog() {
        CreateBlogDTO newBlog = createBlogDTO("TervetuloaTesti", "Tämä on blogin sisältöteksti.");

        ResponseEntity<String> response = createBlog(newBlog);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("User cannot create a blog without required fields")
    void userCannotCreateBlogWithoutRequiredFields() {
        ResponseEntity<String> response1 = createBlog(createBlogDTO(null, "Tämä on blogin sisältöteksti."));
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<String> response2 = createBlog(createBlogDTO("TervetuloaTesti", null));
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("User can edit a blog user owns")
    void userCanEditABlogUserOwns() {
        Integer blogId = createBlogAndGetId(createBlogDTO(TEST_TITLE, TEST_CONTENT));

        ResponseEntity<String> updateResponse = updateBlog(blogId, createBlogDTO(UPDATED_TITLE, UPDATED_CONTENT));

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotBlank();

        assertBlogContent(updateResponse.getBody(), UPDATED_TITLE, UPDATED_CONTENT);

        ResponseEntity<String> getResponse = getBlogById(blogId);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertBlogContent(getResponse.getBody(), UPDATED_TITLE, UPDATED_CONTENT);
    }

    @Test
    @DisplayName("User cannot edit a blog user does not own")
    void userCannotEditABlogUserDoesNotOwn() {
        Integer blogId = createBlogAndGetId(createBlogDTO(TEST_TITLE, TEST_CONTENT));

        HttpEntity<Void> anotherUserEntity = registerAndLoginUser("anotheruser", "another@example.com");

        ResponseEntity<String> updateResponse = updateBlog(blogId, createBlogDTO("Malicious Title", "Malicious Content"), anotherUserEntity);

        assertThat(updateResponse.getStatusCode()).isIn(HttpStatus.FORBIDDEN, HttpStatus.NOT_FOUND, HttpStatus.UNAUTHORIZED);

        ResponseEntity<String> getResponse = getBlogById(blogId);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertBlogContent(getResponse.getBody(), TEST_TITLE, TEST_CONTENT);
    }

    @Test
    @DisplayName("User can delete a blog user owns")
    void userCanDeleteABlogUserOwns() {
        Integer blogId = createBlogAndGetId(createBlogDTO(TEST_TITLE, TEST_CONTENT));

        ResponseEntity<String> deleteResponse = deleteBlog(blogId);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = getBlogById(blogId);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("User cannot delete a blog user does not own")
    void userCannotDeleteABlogUserDoesNotOwn() {
        Integer blogId = createBlogAndGetId(createBlogDTO(TEST_TITLE, TEST_CONTENT));

        HttpEntity<Void> anotherUserEntity = registerAndLoginUser("anotheruser", "another@example.com");

        ResponseEntity<String> deleteResponse = deleteBlog(blogId, anotherUserEntity);

        assertThat(deleteResponse.getStatusCode()).isIn(HttpStatus.FORBIDDEN, HttpStatus.UNAUTHORIZED);

        ResponseEntity<String> getResponse = getBlogById(blogId);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertBlogContent(getResponse.getBody(), TEST_TITLE, TEST_CONTENT);
    }

    private CreateBlogDTO createBlogDTO(String title, String content) {
        return new CreateBlogDTO(title, content, null);
    }

    private HttpEntity<Void> registerAndLoginUser(String username, String email) {
        RegisterDTO registerRequest = new RegisterDTO(username, "Password123!", email);
        restTemplate.postForEntity("/api/auth/register", registerRequest, String.class);

        LoginDTO loginRequest = new LoginDTO(username, "Password123!");
        ResponseEntity<String> loginResponse = restTemplate.postForEntity("/api/auth/login", loginRequest, String.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotBlank();

        DocumentContext ctx = JsonPath.parse(loginResponse.getBody());
        String tokenType = ctx.read("$.tokenType");
        assertThat(tokenType).isEqualTo("Bearer");

        String jwtToken = ctx.read("$.accessToken");
        assertThat(jwtToken).isNotBlank();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }

    private ResponseEntity<String> createBlog(CreateBlogDTO blogDTO) {
        return createBlog(blogDTO, entity);
    }

    private ResponseEntity<String> createBlog(CreateBlogDTO blogDTO, HttpEntity<Void> authEntity) {
        HttpEntity<CreateBlogDTO> requestEntity = new HttpEntity<>(blogDTO, authEntity.getHeaders());
        return restTemplate.exchange("/api/blog/create", HttpMethod.POST, requestEntity, String.class);
    }

    private Integer createBlogAndGetId(CreateBlogDTO blogDTO) {
        ResponseEntity<String> createResponse = createBlog(blogDTO);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        DocumentContext ctx = JsonPath.parse(createResponse.getBody());
        return ctx.read("$.id");
    }

    private ResponseEntity<String> getBlogList() {
        return restTemplate.exchange("/api/blog", HttpMethod.GET, entity, String.class);
    }

    private ResponseEntity<String> getBlogById(Integer blogId) {
        return restTemplate.exchange("/api/blog/" + blogId, HttpMethod.GET, entity, String.class);
    }

    private ResponseEntity<String> updateBlog(Integer blogId, CreateBlogDTO blogDTO) {
        return updateBlog(blogId, blogDTO, entity);
    }

    private ResponseEntity<String> updateBlog(Integer blogId, CreateBlogDTO blogDTO, HttpEntity<Void> authEntity) {
        HttpEntity<CreateBlogDTO> requestEntity = new HttpEntity<>(blogDTO, authEntity.getHeaders());
        return restTemplate.exchange("/api/blog/" + blogId, HttpMethod.PUT, requestEntity, String.class);
    }

    private ResponseEntity<String> deleteBlog(Integer blogId) {
        return deleteBlog(blogId, entity);
    }

    private ResponseEntity<String> deleteBlog(Integer blogId, HttpEntity<Void> authEntity) {
        return restTemplate.exchange("/api/blog/" + blogId, HttpMethod.DELETE, authEntity, String.class);
    }

    private void assertBlogContent(String responseBody, String expectedTitle, String expectedContent) {
        DocumentContext ctx = JsonPath.parse(responseBody);
        String actualTitle = ctx.read("$.title", String.class);
        String actualContent = ctx.read("$.content", String.class);
        assertThat(actualTitle).isEqualTo(expectedTitle);
        assertThat(actualContent).isEqualTo(expectedContent);
    }
}
