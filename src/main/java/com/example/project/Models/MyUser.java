package com.example.project.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "user")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MyUser {
    @Id
    private String aadhar;
    private String emailId;
    private String password;
    private DriverLicense driverLicense;
    private List<String> roles;
    private AccountStatus accountStatus;

    private String reservedVehicle;
}
