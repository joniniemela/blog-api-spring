package com.jmnenterprises.blogapi.repository;

import com.jmnenterprises.blogapi.entity.Tag;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TagRepository extends CrudRepository<Tag, Long> {
    Optional<Tag> findByName(String tagName);
}
