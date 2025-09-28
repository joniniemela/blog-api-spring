package com.jmnenterprises.blogapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlogResponse {
    private Long id;
    private String title;
    private String content;
    private String author_username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
