package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.BlogResponse;
import com.jmnenterprises.blogapi.dto.CreateBlogDTO;
import com.jmnenterprises.blogapi.entity.Blog;
import com.jmnenterprises.blogapi.repository.BlogRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final ModelMapper modelMapper;

    public BlogServiceImpl(BlogRepository blogRepository, ModelMapper modelMapper) {
        this.blogRepository = blogRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BlogResponse createBlog(CreateBlogDTO createBlogDTO) {
        Blog blog = modelMapper.map(createBlogDTO, Blog.class);
        blog.setCreatedAt(LocalDateTime.now());
        Blog save = blogRepository.save(blog);
        return modelMapper.map(save, BlogResponse.class);
    }
}
