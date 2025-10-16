package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.response.BlogResponse;
import com.jmnenterprises.blogapi.dto.request.CreateBlogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface BlogService {
    BlogResponse createBlog(CreateBlogDTO createBlogDTO, String author);
    Page<BlogResponse> findAll(PageRequest pageRequest);
    BlogResponse findById(Long id);
    BlogResponse editBlog(Long id, CreateBlogDTO createBlogDTO, String username);
    void deleteBlog(Long id, String username);
}
