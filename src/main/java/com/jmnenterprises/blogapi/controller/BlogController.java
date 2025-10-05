package com.jmnenterprises.blogapi.controller;

import com.jmnenterprises.blogapi.dto.BlogResponse;
import com.jmnenterprises.blogapi.dto.CreateBlogDTO;
import com.jmnenterprises.blogapi.dto.CreateBlogResponse;
import com.jmnenterprises.blogapi.service.BlogService;
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

    @GetMapping("/{id}")
    private ResponseEntity<BlogResponse> getById(@PathVariable Long id){
        try {
            BlogResponse blog = blogService.findById(id);
            return ResponseEntity.ok(blog);
        } catch(RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping("/create")
    private ResponseEntity<Object> post(@Valid @RequestBody CreateBlogDTO createBlogDTO, Principal principal) {
        try {
            String author = principal.getName();
            CreateBlogResponse response = blogService.createBlog(createBlogDTO, author);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    private ResponseEntity<Object> put(@PathVariable Long id, @Valid @RequestBody CreateBlogDTO createBlogDTO, Principal principal) {
        try {
            String username = principal.getName();
            BlogResponse response = blogService.editBlog(id, createBlogDTO, username);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Object> delete(@PathVariable Long id, Principal principal) {
        try {
            String username = principal.getName();
            blogService.deleteBlog(id, username);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
