package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.response.TagResponse;
import com.jmnenterprises.blogapi.entity.Tag;
import com.jmnenterprises.blogapi.repository.TagRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
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

    @Override
    public TagResponse createTag(String name) {
        Tag tag = Tag.builder()
                .name(name)
                .build();
        Tag savedTag = tagRepository.save(tag);
        return modelMapper.map(savedTag, TagResponse.class);
    }

    @Override
    public TagResponse findById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
        return modelMapper.map(tag, TagResponse.class);
    }

    @Override
    public void deleteById(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new RuntimeException("Tag not found with id: " + id);
        }
        tagRepository.deleteById(id);
    }
}
