package com.example.project.Models.Repository;

import com.example.project.Models.JwtToken;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * JwtTokenRepository
 */
public interface JwtTokenRepository extends MongoRepository<JwtToken, String> {
}
