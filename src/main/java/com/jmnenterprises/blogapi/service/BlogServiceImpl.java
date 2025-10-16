package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.response.BlogResponse;
import com.jmnenterprises.blogapi.dto.request.CreateBlogDTO;
import com.jmnenterprises.blogapi.entity.Blog;
import com.jmnenterprises.blogapi.entity.Tag;
import com.jmnenterprises.blogapi.entity.User;
import com.jmnenterprises.blogapi.repository.AuthRepository;
import com.jmnenterprises.blogapi.repository.BlogRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
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
    public BlogResponse createBlog(CreateBlogDTO createBlogDTO, String author) {
        Blog blog = modelMapper.map(createBlogDTO, Blog.class);
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());

        User user = authRepository.findByUsername(author);
        blog.setAuthor(user);

        Blog save = blogRepository.save(blog);
        BlogResponse response = modelMapper.map(save, BlogResponse.class);
        response.setAuthorUsername(author);
        response.setTagNames(extractTagNames(save));
        return response;
    }

    @Override
    public Page<BlogResponse> findAll(PageRequest pageRequest) {
        Page<Blog> blogs = blogRepository.findAll(pageRequest);
        return blogs.map(this::mapToBlogResponse);
    }

    @Override
    public BlogResponse findById(Long id) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Blog not found"));
        return mapToBlogResponse(blog);
    }

    @Override
    public BlogResponse editBlog(Long id, CreateBlogDTO createBlogDTO, String username) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Blog not found"));

        if (blog.getAuthor() == null || !blog.getAuthor().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to edit this blog");
        }

        blog.setTitle(createBlogDTO.getTitle());
        blog.setContent(createBlogDTO.getContent());
        blog.setUpdatedAt(LocalDateTime.now());

        Blog save = blogRepository.save(blog);
        return mapToBlogResponse(save);
    }

    @Override
    public void deleteBlog(Long id, String username) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Blog not found"));
        if (blog.getAuthor() == null || !blog.getAuthor().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to delete this blog");
        }
        blogRepository.deleteById(id);
    }

    private BlogResponse mapToBlogResponse(Blog blog) {
        BlogResponse response = modelMapper.map(blog, BlogResponse.class);
        String username = blog.getAuthor() != null ? blog.getAuthor().getUsername() : "unknown";
        response.setAuthorUsername(username);
        response.setTagNames(extractTagNames(blog));
        return response;
    }

    private Set<String> extractTagNames(Blog blog) {
        if (blog.getTags() == null || blog.getTags().isEmpty()) {
            return new HashSet<>();
        }
        return blog.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
    }
}
