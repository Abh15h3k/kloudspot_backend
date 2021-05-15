package com.example.project.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "vehicle")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    @Id
    private String registration;
    private GeoLocation geoLocation;
    private VehicleCategory Category;
    private String make;
    private String model;
    @Indexed
    private String reservedBy;

    @Transient
    private double distanceFromUser;

    private List<String> tripIds = new ArrayList<>();
    private List<String> transactions = new ArrayList<>();

    public void addTripId(String tripId) {
        this.tripIds.add(tripId);
    }
}
