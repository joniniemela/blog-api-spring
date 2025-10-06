package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.TagResponse;
import com.jmnenterprises.blogapi.entity.Tag;
import com.jmnenterprises.blogapi.repository.TagRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;

    public TagServiceImpl(TagRepository tagRepository, ModelMapper modelMapper) {
        this.tagRepository = tagRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<TagResponse> findAll(PageRequest pageRequest) {
        Page<Tag> tags = tagRepository.findAll(pageRequest);
        return tags.map(tag -> modelMapper.map(tag, TagResponse.class));
    }
}
