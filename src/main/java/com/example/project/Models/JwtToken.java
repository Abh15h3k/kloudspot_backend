package com.example.project.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "jwt_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtToken {
    @Id
    private String username;
    private String token;
}
