package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.BlogResponse;
import com.jmnenterprises.blogapi.dto.CreateBlogDTO;
import com.jmnenterprises.blogapi.dto.CreateBlogResponse;
import com.jmnenterprises.blogapi.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface BlogService {
    CreateBlogResponse createBlog(CreateBlogDTO createBlogDTO, String author);

    Page<BlogResponse> findAll(PageRequest pageRequest);
}
