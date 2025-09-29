package com.jmnenterprises.blogapi.controller;

import com.jmnenterprises.blogapi.dto.BlogResponse;
import com.jmnenterprises.blogapi.dto.CreateBlogDTO;
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

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/blog")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping
    private ResponseEntity<List<BlogResponse>> getAll(Pageable pageable){
        Page<BlogResponse> page = blogService.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "title"))
                ));

        return ResponseEntity.ok(page.getContent());

    }

    @PostMapping("/create")
    private ResponseEntity<Object> post(@Valid @RequestBody CreateBlogDTO createBlogDTO, Principal principal){
        try {
            String author = principal.getName();
            BlogResponse response = blogService.createBlog(createBlogDTO, author);
            return ResponseHandler.generateResponse("New blog created successfully", HttpStatus.CREATED, response);
        } catch(RuntimeException e) {
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

}
