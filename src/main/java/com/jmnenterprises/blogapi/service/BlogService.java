package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.BlogResponse;
import com.jmnenterprises.blogapi.dto.CreateBlogDTO;


public interface BlogService {
    BlogResponse createBlog(CreateBlogDTO createBlogDTO);
}
