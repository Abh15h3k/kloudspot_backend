package com.example.project.Models.Forms;

import com.example.project.Models.AccountStatus;
import com.example.project.Models.MyUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAccountsStatusForm {
    private MyUser[] users;
}
