package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.BlogResponse;
import com.jmnenterprises.blogapi.dto.CreateBlogDTO;
import com.jmnenterprises.blogapi.dto.CreateBlogResponse;
import com.jmnenterprises.blogapi.entity.Blog;
import com.jmnenterprises.blogapi.entity.User;
import com.jmnenterprises.blogapi.repository.AuthRepository;
import com.jmnenterprises.blogapi.repository.BlogRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final ModelMapper modelMapper;
    private final AuthRepository authRepository;

    public BlogServiceImpl(BlogRepository blogRepository, ModelMapper modelMapper, AuthRepository authRepository) {
        this.blogRepository = blogRepository;
        this.modelMapper = modelMapper;
        this.authRepository = authRepository;
    }

    @Override
    public CreateBlogResponse createBlog(CreateBlogDTO createBlogDTO, String author) {
        Blog blog = modelMapper.map(createBlogDTO, Blog.class);
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
        User user = authRepository.findByUsername(author);
        blog.setAuthor(user);
        Blog save = blogRepository.save(blog);
        return modelMapper.map(save, CreateBlogResponse.class);
    }

    @Override
    public Page<BlogResponse> findAll(PageRequest pageRequest) {
        Page<Blog> blogs = blogRepository.findAll(pageRequest);
        return blogs.map(blog -> {
            BlogResponse response = modelMapper.map(blog, BlogResponse.class);
            response.setAuthor_username(blog.getAuthor().getUsername());
            return response;
        });
    }
}
