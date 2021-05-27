package com.example.project.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "user")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MyUser {
    @Id
    private String aadhar;

    private String fullName;

    @Indexed
    private String emailId;
    private String password;

    @Indexed
    private DriverLicense driverLicense;
    private List<String> roles;
    private AccountStatus accountStatus;

    @Indexed
    private String reservedVehicle;

    @Indexed
    private Trip activeTrip;
    private List<String> tripIds;
    private List<String> transactions;

    private String orderId;
    private LocalDateTime joinDate;

    public void addTripId(String tripId) {
        this.tripIds.add(tripId);
    }
}
