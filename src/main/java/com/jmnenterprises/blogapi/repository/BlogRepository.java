package com.jmnenterprises.blogapi.repository;

import com.jmnenterprises.blogapi.entity.Blog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BlogRepository extends CrudRepository<Blog, Long>, PagingAndSortingRepository<Blog, Long> {
}
