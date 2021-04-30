package com.example.project.Models.Forms;

import com.example.project.Models.VehicleCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyVehicleForm {
    private String registration;
    private VehicleCategory category;
    private String make;
    private String model;
}
