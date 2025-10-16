package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.dto.response.TagResponse;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TagServiceImpl tagService;

    private Tag tag;
    private TagResponse tagResponse;

    @BeforeEach
    void setUp() {
        tag = Tag.builder()
                .id(1L)
                .name("Technology")
                .build();

        tagResponse = new TagResponse();
        tagResponse.setId(1L);
        tagResponse.setName("Technology");
    }

    @Nested
    @DisplayName("Find All Tags Tests")
    class FindAllTagsTests {

        @Test
        @DisplayName("Should return paginated tags")
        void shouldReturnPaginatedTags() {
            PageRequest pageRequest = PageRequest.of(0, 10);
            Tag tag2 = Tag.builder().id(2L).name("Science").build();
            Page<Tag> tagPage = new PageImpl<>(Arrays.asList(tag, tag2));

            TagResponse tagResponse2 = new TagResponse();
            tagResponse2.setId(2L);
            tagResponse2.setName("Science");

            when(tagRepository.findAll(pageRequest)).thenReturn(tagPage);
            when(modelMapper.map(tag, TagResponse.class)).thenReturn(tagResponse);
            when(modelMapper.map(tag2, TagResponse.class)).thenReturn(tagResponse2);

            Page<TagResponse> result = tagService.findAll(pageRequest);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Technology");
            assertThat(result.getContent().get(1).getName()).isEqualTo("Science");
            verify(tagRepository).findAll(pageRequest);
        }

        @Test
        @DisplayName("Should return empty page when no tags exist")
        void shouldReturnEmptyPageWhenNoTagsExist() {
            PageRequest pageRequest = PageRequest.of(0, 10);
            Page<Tag> emptyPage = new PageImpl<>(List.of());

            when(tagRepository.findAll(pageRequest)).thenReturn(emptyPage);

            Page<TagResponse> result = tagService.findAll(pageRequest);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            verify(tagRepository).findAll(pageRequest);
        }
    }

    @Nested
    @DisplayName("Create Tag Tests")
    class CreateTagTests {

        @Test
        @DisplayName("Should create a new tag")
        void shouldCreateNewTag() {
            String tagName = "Technology";

            when(tagRepository.save(any(Tag.class))).thenReturn(tag);
            when(modelMapper.map(tag, TagResponse.class)).thenReturn(tagResponse);

            TagResponse result = tagService.createTag(tagName);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Technology");
            verify(tagRepository).save(any(Tag.class));
            verify(modelMapper).map(tag, TagResponse.class);
        }
    }

    @Nested
    @DisplayName("Find Tag By Id Tests")
    class FindTagByIdTests {

        @Test
        @DisplayName("Should return tag when found by id")
        void shouldReturnTagWhenFoundById() {
            when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
            when(modelMapper.map(tag, TagResponse.class)).thenReturn(tagResponse);

            TagResponse result = tagService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Technology");
            verify(tagRepository).findById(1L);
            verify(modelMapper).map(tag, TagResponse.class);
        }

        @Test
        @DisplayName("Should throw exception when tag not found")
        void shouldThrowExceptionWhenTagNotFound() {
            when(tagRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tagService.findById(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Tag not found with id: 99");

            verify(tagRepository).findById(99L);
            verify(modelMapper, never()).map(any(), eq(TagResponse.class));
        }
    }

    @Nested
    @DisplayName("Delete Tag Tests")
    class DeleteTagTests {

        @Test
        @DisplayName("Should delete tag when it exists")
        void shouldDeleteTagWhenItExists() {
            when(tagRepository.existsById(1L)).thenReturn(true);
            doNothing().when(tagRepository).deleteById(1L);

            tagService.deleteById(1L);

            verify(tagRepository).existsById(1L);
            verify(tagRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent tag")
        void shouldThrowExceptionWhenDeletingNonExistentTag() {
            when(tagRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> tagService.deleteById(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Tag not found with id: 99");

            verify(tagRepository).existsById(99L);
            verify(tagRepository, never()).deleteById(99L);
        }
    }
}

