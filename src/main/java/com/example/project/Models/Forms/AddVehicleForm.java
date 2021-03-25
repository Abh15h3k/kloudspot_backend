package com.example.project.Models.Forms;

import com.example.project.Models.GeoLocation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddVehicleForm {
    private String registration;
    private String category;
    private GeoLocation geoLocation;
}
