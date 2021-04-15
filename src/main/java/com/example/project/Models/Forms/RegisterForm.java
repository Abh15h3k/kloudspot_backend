package com.example.project.Models.Forms;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterForm {
    private String aadhar;
    private String emailId;
    private String password;
    private String dlnumber;
    private MultipartFile image;
}
