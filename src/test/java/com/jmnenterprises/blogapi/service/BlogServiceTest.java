package com.jmnenterprises.blogapi.service;


import com.jmnenterprises.blogapi.dto.BlogResponse;
import com.jmnenterprises.blogapi.dto.CreateBlogDTO;
import com.jmnenterprises.blogapi.dto.CreateBlogResponse;
import com.jmnenterprises.blogapi.entity.Blog;
import com.jmnenterprises.blogapi.entity.User;
import com.jmnenterprises.blogapi.repository.AuthRepository;
import com.jmnenterprises.blogapi.repository.BlogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class BlogServiceTest {

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    BlogServiceImpl blogService;

    @Test
    void allBlogsAreReturned() {
        Blog blog1 = new Blog();
        Blog blog2 = new Blog();
        User author = new User();
        author.setUsername("testuser");
        blog1.setAuthor(author);
        blog2.setAuthor(author);
        List<Blog> blogs = List.of(blog1, blog2);
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(blogRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(blogs));
        when(modelMapper.map(blog1, BlogResponse.class)).thenReturn(new BlogResponse());
        when(modelMapper.map(blog2, BlogResponse.class)).thenReturn(new BlogResponse());

        var result = blogService.findAll(pageRequest);

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void blogIsCreated() {
        CreateBlogDTO createBlogDTO = new CreateBlogDTO("TestTitle", "TestContent");
        Blog blog = new Blog();
        blog.setTitle(createBlogDTO.getTitle());
        blog.setContent(createBlogDTO.getContent());
        User author = new User();
        author.setUsername("testuser");
        blog.setAuthor(author);

        when(modelMapper.map(createBlogDTO, Blog.class)).thenReturn(blog);
        when(blogRepository.save(blog)).thenReturn(blog);

        CreateBlogResponse expectedResponse = new CreateBlogResponse();
        expectedResponse.setTitle("TestTitle");
        expectedResponse.setContent("TestContent");
        expectedResponse.setAuthorUsername("testuser");

        when(modelMapper.map(blog, CreateBlogResponse.class)).thenReturn(expectedResponse);

        CreateBlogResponse result = blogService.createBlog(createBlogDTO, "testuser");

        assertThat(result.getTitle()).isEqualTo("TestTitle");
        assertThat(result.getContent()).isEqualTo("TestContent");
        assertThat(result.getAuthorUsername()).isEqualTo("testuser");
    }


}
