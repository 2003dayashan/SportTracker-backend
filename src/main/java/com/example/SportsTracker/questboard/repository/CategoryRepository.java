package com.example.SportsTracker.questboard.repository;

import com.example.SportsTracker.questboard.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {
    Optional<Category> findById(String id);
    List<Category> findByName(String name);
    Page<Category> findByName(String name, Pageable pageable);
    Page<Category> findAll(Pageable pageable);
}