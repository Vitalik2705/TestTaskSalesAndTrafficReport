package com.task.repository;

import com.task.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    @Query("{username:'?0'}")
    Optional<User> findUserByUsername(String username);
    boolean existsByUsername(String username);
}
