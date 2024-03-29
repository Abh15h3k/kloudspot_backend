package com.example.project.Models.Forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileForm {
    private String fullName;
    private String aadhar;
    private String emailId;
    private String driverLicenseNumber;
}
