package com.example.project.Controller;

import java.util.List;

import com.example.project.Models.GenericResponse;
import com.example.project.Models.GeoLocation;
import com.example.project.Models.Vehicle;
import com.example.project.Models.Forms.VehicleNearMe;
import com.example.project.Models.Repository.VehicleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "/vehicle")
public class VehicleController {

    @Autowired private VehicleRepository vehicleRepository;

    @GetMapping(path = "/all")
    public ResponseEntity<GenericResponse> getAllVehicles() {
        GenericResponse genericResponse = new GenericResponse();
        List<Vehicle> vehicles = vehicleRepository.findAll();

        if (vehicles.isEmpty()) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("No vehicles");
        } else {
            genericResponse.setBody(vehicles);
        }

        return ResponseEntity.ok(genericResponse);
    }

    @PostMapping(path = "/nearme")
    public GenericResponse vehicleNearMe(@RequestBody VehicleNearMe vehicleNearMe) {
        GenericResponse genericResponse = new GenericResponse();

        List<Vehicle> vehicles = vehicleRepository.findAll();
        vehicles.removeIf((vehicle) -> {
            return (GeoLocation.distanceBetweenLocations(vehicleNearMe.getGeoLocation(), vehicle.getGeoLocation()) > vehicleNearMe.getRadius());
        });

        if (vehicles.isEmpty()) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("No Vehicles near by");
        } else {
            genericResponse.setError(false);
            genericResponse.setBody(vehicles);
        }

        return genericResponse;
    }
}
