package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.TagResponse;
import com.jmnenterprises.blogapi.entity.Tag;
import com.jmnenterprises.blogapi.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TagServiceImpl tagService;

    private Tag tag1;
    private Tag tag2;
    private TagResponse tagResponse1;
    private TagResponse tagResponse2;

    @BeforeEach
    void setUp() {
        tag1 = Tag.builder()
                .id(1L)
                .name("Java")
                .blogs(new HashSet<>())
                .build();

        tag2 = Tag.builder()
                .id(2L)
                .name("Spring")
                .blogs(new HashSet<>())
                .build();

        tagResponse1 = new TagResponse();
        tagResponse2 = new TagResponse();
    }

    @Nested
    @DisplayName("FindAll Tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return paginated tags")
        void shouldReturnPaginatedTags() {
            PageRequest pageRequest = PageRequest.of(0, 10);
            List<Tag> tags = Arrays.asList(tag1, tag2);
            Page<Tag> tagPage = new PageImpl<>(tags, pageRequest, tags.size());

            when(tagRepository.findAll(pageRequest)).thenReturn(tagPage);
            when(modelMapper.map(tag1, TagResponse.class)).thenReturn(tagResponse1);
            when(modelMapper.map(tag2, TagResponse.class)).thenReturn(tagResponse2);

            Page<TagResponse> result = tagService.findAll(pageRequest);

            assertNotNull(result);
            assertEquals(2, result.getTotalElements());
            assertEquals(2, result.getContent().size());
            verify(tagRepository).findAll(pageRequest);
            verify(modelMapper, times(2)).map(any(Tag.class), eq(TagResponse.class));
        }

        @Test
        @DisplayName("Should return empty page when no tags exist")
        void shouldReturnEmptyPageWhenNoTagsExist() {
            PageRequest pageRequest = PageRequest.of(0, 10);
            Page<Tag> emptyPage = new PageImpl<>(List.of(), pageRequest, 0);

            when(tagRepository.findAll(pageRequest)).thenReturn(emptyPage);

            Page<TagResponse> result = tagService.findAll(pageRequest);

            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());
            verify(tagRepository).findAll(pageRequest);
            verify(modelMapper, never()).map(any(Tag.class), eq(TagResponse.class));
        }

        @Test
        @DisplayName("Should handle pagination correctly")
        void shouldHandlePaginationCorrectly() {
            PageRequest pageRequest = PageRequest.of(1, 1);
            List<Tag> tags = List.of(tag2);
            Page<Tag> tagPage = new PageImpl<>(tags, pageRequest, 2);

            when(tagRepository.findAll(pageRequest)).thenReturn(tagPage);
            when(modelMapper.map(tag2, TagResponse.class)).thenReturn(tagResponse2);

            Page<TagResponse> result = tagService.findAll(pageRequest);

            assertNotNull(result);
            assertEquals(2, result.getTotalElements());
            assertEquals(1, result.getContent().size());
            assertEquals(1, result.getNumber());
            verify(tagRepository).findAll(pageRequest);
        }
    }
}
