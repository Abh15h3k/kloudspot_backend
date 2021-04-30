package com.example.project.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverLicense {
    @Indexed(unique = true)
    private String licenseNumber;
    private byte[] imageData;
}
