package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.BlogResponse;
import com.jmnenterprises.blogapi.dto.CreateBlogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface BlogService {
    BlogResponse createBlog(CreateBlogDTO createBlogDTO, String author);

    Page<BlogResponse> findAll(PageRequest pageRequest);
}
