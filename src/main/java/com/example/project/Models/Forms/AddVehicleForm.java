package com.example.project.Models.Forms;

import com.example.project.Models.GeoLocation;

import com.example.project.Models.VehicleCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddVehicleForm {
    private String registration;
    private VehicleCategory category;
    private GeoLocation geoLocation;
}
