package com.jmnenterprises.blogapi.controller;

import com.jmnenterprises.blogapi.dto.CreateBlogDTO;
import com.jmnenterprises.blogapi.entity.Blog;
import com.jmnenterprises.blogapi.repository.BlogRepository;
import com.jmnenterprises.blogapi.service.BlogService;
import com.jmnenterprises.blogapi.utils.ResponseHandler;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/blog")
public class BlogController {

    private final BlogRepository blogRepository;
    private final BlogService blogService;

    public BlogController(BlogRepository blogRepository, BlogService blogService) {
        this.blogRepository = blogRepository;
        this.blogService = blogService;
    }

    @GetMapping
    private ResponseEntity<List<Blog>> getAll(Pageable pageable){
        Page<Blog> page = blogRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "title"))
                ));

        return ResponseEntity.ok(page.getContent());

    }

    @PostMapping("/create")
    private ResponseEntity<Object> post(@Valid @RequestBody CreateBlogDTO createBlogDTO){
        try {
            Object result = blogService.createBlog(createBlogDTO);
            return ResponseHandler.generateResponse("New blog created successfully", HttpStatus.CREATED, result);
        } catch(RuntimeException e) {
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

}
