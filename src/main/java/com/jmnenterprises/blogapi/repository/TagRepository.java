package com.jmnenterprises.blogapi.repository;

import com.jmnenterprises.blogapi.entity.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TagRepository extends CrudRepository<Tag, Long>, PagingAndSortingRepository<Tag, Long> {
}
