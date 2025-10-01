package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.BlogResponse;
import com.jmnenterprises.blogapi.dto.CreateBlogDTO;
import com.jmnenterprises.blogapi.dto.CreateBlogResponse;
import com.jmnenterprises.blogapi.entity.Blog;
import com.jmnenterprises.blogapi.entity.User;
import com.jmnenterprises.blogapi.repository.AuthRepository;
import com.jmnenterprises.blogapi.repository.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BlogService Tests")
class BlogServiceTest {

    // Test constants
    private static final Long BLOG_ID = 1L;
    private static final String USERNAME = "testuser";
    private static final String BLOG_TITLE = "Test Blog Title";
    private static final String BLOG_CONTENT = "Test blog content";
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BlogServiceImpl blogService;

    private User testUser;
    private Blog testBlog;
    private CreateBlogDTO createBlogDTO;
    private PageRequest pageRequest;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testBlog = createTestBlog();
        createBlogDTO = new CreateBlogDTO(BLOG_TITLE, BLOG_CONTENT);
        pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    }

    @Nested
    @DisplayName("Find All Blogs")
    class FindAllBlogsTests {

        @Test
        @DisplayName("Should return paginated blogs with author usernames")
        void shouldReturnPaginatedBlogsWithAuthorUsernames() {
            // Given
            Blog anotherBlog = createAnotherTestBlog();
            List<Blog> blogs = List.of(testBlog, anotherBlog);
            Page<Blog> blogPage = new PageImpl<>(blogs, pageRequest, blogs.size());

            BlogResponse blogResponse1 = createBlogResponse(BLOG_ID, BLOG_TITLE, USERNAME);
            BlogResponse blogResponse2 = createBlogResponse(2L, "Another Title", USERNAME);

            when(blogRepository.findAll(pageRequest)).thenReturn(blogPage);
            when(modelMapper.map(testBlog, BlogResponse.class)).thenReturn(blogResponse1);
            when(modelMapper.map(anotherBlog, BlogResponse.class)).thenReturn(blogResponse2);

            // When
            Page<BlogResponse> result = blogService.findAll(pageRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getNumber()).isEqualTo(PAGE_NUMBER);
            assertThat(result.getSize()).isEqualTo(PAGE_SIZE);

            // Verify all blog responses have author usernames set
            result.getContent().forEach(blogResponse ->
                assertThat(blogResponse.getAuthorUsername()).isEqualTo(USERNAME)
            );

            verify(blogRepository).findAll(pageRequest);
            verify(modelMapper).map(testBlog, BlogResponse.class);
            verify(modelMapper).map(anotherBlog, BlogResponse.class);
        }

        @Test
        @DisplayName("Should handle blogs with null authors gracefully")
        void shouldHandleBlogsWithNullAuthorsGracefully() {
            // Given
            Blog blogWithoutAuthor = createTestBlogWithoutAuthor();
            List<Blog> blogs = List.of(blogWithoutAuthor);
            Page<Blog> blogPage = new PageImpl<>(blogs, pageRequest, blogs.size());

            BlogResponse blogResponse = createBlogResponse(BLOG_ID, BLOG_TITLE, null);

            when(blogRepository.findAll(pageRequest)).thenReturn(blogPage);
            when(modelMapper.map(blogWithoutAuthor, BlogResponse.class)).thenReturn(blogResponse);

            // When
            Page<BlogResponse> result = blogService.findAll(pageRequest);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().getFirst().getAuthorUsername()).isEqualTo("unknown");
        }
    }

    @Nested
    @DisplayName("Find Blog By ID")
    class FindBlogByIdTests {

        @Test
        @DisplayName("Should return blog when valid ID is provided")
        void shouldReturnBlogWhenValidIdIsProvided() {
            // Given
            BlogResponse expectedResponse = createBlogResponse(BLOG_ID, BLOG_TITLE, USERNAME);

            when(blogRepository.findById(BLOG_ID)).thenReturn(Optional.of(testBlog));
            when(modelMapper.map(testBlog, BlogResponse.class)).thenReturn(expectedResponse);

            // When
            BlogResponse result = blogService.findById(BLOG_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(BLOG_ID);
            assertThat(result.getTitle()).isEqualTo(BLOG_TITLE);
            assertThat(result.getAuthorUsername()).isEqualTo(USERNAME);

            verify(blogRepository).findById(BLOG_ID);
            verify(modelMapper).map(testBlog, BlogResponse.class);
        }

        @Test
        @DisplayName("Should throw exception when blog not found")
        void shouldThrowExceptionWhenBlogNotFound() {
            // Given
            Long nonExistentId = 999L;
            when(blogRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> blogService.findById(nonExistentId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Blog not found");

            verify(blogRepository).findById(nonExistentId);
            verifyNoInteractions(modelMapper);
        }

        @Test
        @DisplayName("Should handle blog with null author")
        void shouldHandleBlogWithNullAuthor() {
            // Given
            Blog blogWithoutAuthor = createTestBlogWithoutAuthor();
            BlogResponse expectedResponse = createBlogResponse(BLOG_ID, BLOG_TITLE, null);

            when(blogRepository.findById(BLOG_ID)).thenReturn(Optional.of(blogWithoutAuthor));
            when(modelMapper.map(blogWithoutAuthor, BlogResponse.class)).thenReturn(expectedResponse);

            // When
            BlogResponse result = blogService.findById(BLOG_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getAuthorUsername()).isEqualTo("unknown");
        }
    }

    @Nested
    @DisplayName("Create Blog")
    class CreateBlogTests {

        @Test
        @DisplayName("Should successfully create blog with valid data")
        void shouldSuccessfullyCreateBlogWithValidData() {
            // Given
            Blog mappedBlog = createTestBlogForCreation();
            Blog savedBlog = createTestBlogWithId();
            CreateBlogResponse expectedResponse = createBlogResponseForCreation();

            when(modelMapper.map(createBlogDTO, Blog.class)).thenReturn(mappedBlog);
            when(authRepository.findByUsername(USERNAME)).thenReturn(testUser);
            when(blogRepository.save(any(Blog.class))).thenReturn(savedBlog);
            when(modelMapper.map(savedBlog, CreateBlogResponse.class)).thenReturn(expectedResponse);

            // When
            CreateBlogResponse result = blogService.createBlog(createBlogDTO, USERNAME);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo(BLOG_TITLE);
            assertThat(result.getContent()).isEqualTo(BLOG_CONTENT);
            assertThat(result.getAuthorUsername()).isEqualTo(USERNAME);

            verify(modelMapper).map(createBlogDTO, Blog.class);
            verify(authRepository).findByUsername(USERNAME);
            verify(blogRepository).save(any(Blog.class));
            verify(modelMapper).map(savedBlog, CreateBlogResponse.class);
        }

        @Test
        @DisplayName("Should set timestamps when creating blog")
        void shouldSetTimestampsWhenCreatingBlog() {
            // Given
            Blog mappedBlog = createTestBlogForCreation();
            when(modelMapper.map(createBlogDTO, Blog.class)).thenReturn(mappedBlog);
            when(authRepository.findByUsername(USERNAME)).thenReturn(testUser);
            when(blogRepository.save(any(Blog.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(modelMapper.map(any(Blog.class), eq(CreateBlogResponse.class))).thenReturn(new CreateBlogResponse());

            // When
            blogService.createBlog(createBlogDTO, USERNAME);

            // Then
            verify(blogRepository).save(argThat(blog ->
                blog.getCreatedAt() != null && blog.getUpdatedAt() != null
            ));
        }
    }

    // Helper methods for creating test objects
    private User createTestUser() {
        User user = new User();
        user.setUsername(USERNAME);
        return user;
    }

    private Blog createTestBlog() {
        Blog blog = new Blog();
        blog.setId(BLOG_ID);
        blog.setTitle(BLOG_TITLE);
        blog.setContent(BLOG_CONTENT);
        blog.setAuthor(testUser);
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
        return blog;
    }

    private Blog createAnotherTestBlog() {
        Blog blog = new Blog();
        blog.setId(2L);
        blog.setTitle("Another Title");
        blog.setContent("Another content");
        blog.setAuthor(testUser);
        return blog;
    }

    private Blog createTestBlogWithoutAuthor() {
        Blog blog = new Blog();
        blog.setId(BLOG_ID);
        blog.setTitle(BLOG_TITLE);
        blog.setContent(BLOG_CONTENT);
        blog.setAuthor(null);
        return blog;
    }

    private Blog createTestBlogForCreation() {
        Blog blog = new Blog();
        blog.setTitle(BLOG_TITLE);
        blog.setContent(BLOG_CONTENT);
        return blog;
    }

    private Blog createTestBlogWithId() {
        Blog blog = createTestBlogForCreation();
        blog.setId(BLOG_ID);
        blog.setAuthor(testUser);
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
        return blog;
    }

    private BlogResponse createBlogResponse(Long id, String title, String authorUsername) {
        BlogResponse response = new BlogResponse();
        response.setId(id);
        response.setTitle(title);
        response.setAuthorUsername(authorUsername);
        return response;
    }

    private CreateBlogResponse createBlogResponseForCreation() {
        CreateBlogResponse response = new CreateBlogResponse();
        response.setTitle(BLOG_TITLE);
        response.setContent(BLOG_CONTENT);
        response.setAuthorUsername(USERNAME);
        return response;
    }
}
