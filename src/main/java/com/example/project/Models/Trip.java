package com.example.project.Models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "trip")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trip {
    @Id
    private String tripId;

    @Indexed
    private String vehicleRegistration;
    @Indexed
    private String userAadhar;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public boolean isCompleted() {
        return (endDateTime != null);
    }
}
