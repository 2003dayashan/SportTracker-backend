package com.example.SportsTracker.questboard.repository;

import com.example.SportsTracker.questboard.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("questUserRepository")
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(User.Role role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}