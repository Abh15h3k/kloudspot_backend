package com.example.project.Models.Forms;

import com.example.project.Models.AccountStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAccountStatusForm {
    private String aadhar;
    private AccountStatus status;
}
