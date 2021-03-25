package com.example.project.Models.Repository;

import com.example.project.Models.MyUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyUserRepository extends MongoRepository<MyUser, String> {
    public Optional<MyUser> findByEmailId(String emailId);
}
