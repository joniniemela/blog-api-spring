package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.TagResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface TagService {
    Page<TagResponse> findAll(PageRequest pageRequest);
    TagResponse createTag(String name);
    TagResponse findById(Long id);
    void deleteById(Long id);
}
