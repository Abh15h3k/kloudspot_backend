package com.example.project.Controller;

import java.util.List;

import com.example.project.Models.GenericResponse;
import com.example.project.Models.GeoLocation;
import com.example.project.Models.Vehicle;
import com.example.project.Models.Forms.VehicleNearMe;
import com.example.project.Models.Dao.VehicleRepository;

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

        genericResponse.setBody(vehicles);

        return ResponseEntity.ok(genericResponse);
    }

    @PostMapping(path = "/nearme")
    public GenericResponse vehicleNearMe(@RequestBody VehicleNearMe vehicleNearMe) {
        GenericResponse genericResponse = new GenericResponse();

        List<Vehicle> vehicles = vehicleRepository.findAll();
        vehicles.removeIf((vehicle) -> {
            if (vehicle.getReservedBy() != null) {
                return true;
            }
            double distance = GeoLocation.distanceBetweenLocations(vehicleNearMe.getGeoLocation(), vehicle.getGeoLocation());
            if (distance <= vehicleNearMe.getRadius()) {
                vehicle.setDistanceFromUser(distance);
                return false;
            } else {
                return true;
            }
//            return ( > vehicleNearMe.getRadius());
        });

        System.out.println(vehicles.isEmpty());
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
