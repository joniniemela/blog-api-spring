package com.jmnenterprises.blogapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlogResponse {
    private Long id;
    private String title;
    private String content;
    private Set<String> tagNames;
    private String authorUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
