package com.example.project.Controller;

import java.util.Optional;

import com.example.project.Models.AccountStatus;
import com.example.project.Models.GenericResponse;
import com.example.project.Models.MyUser;
import com.example.project.Models.Vehicle;
import com.example.project.Models.Forms.AddVehicleForm;
import com.example.project.Models.Forms.UpdateAccountStatusForm;
import com.example.project.Models.Repository.MyUserRepository;
import com.example.project.Models.Repository.VehicleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "/admin")
public class AdminController {

    @Autowired
    private MyUserRepository myUserRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    @GetMapping(path = "/")
    public ResponseEntity<?> adminCheck() {
        return ResponseEntity.ok("Welcome Admin.");
    }

    @PostMapping(path = "/updateaccount")
    public ResponseEntity<GenericResponse> verifyUser(@RequestBody UpdateAccountStatusForm updateAccountStatusForm) {
        GenericResponse genericResponse = new GenericResponse();
        Optional<MyUser> myUserOptional = myUserRepository.findById(updateAccountStatusForm.getAadhar());

        if (myUserOptional.isEmpty()) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("User not found.");
        } else {
            MyUser myUser = myUserOptional.get();
            myUser.setAccountStatus(updateAccountStatusForm.getStatus());
            myUserRepository.save(myUser);
            genericResponse.setBody("User updated successfully.");
        }

        return ResponseEntity.ok(genericResponse);
    }

    @PostMapping(path = "/vehicle/add")
    public ResponseEntity<GenericResponse> addVehicle(@RequestBody AddVehicleForm addVehicleForm) {
        GenericResponse genericResponse = new GenericResponse();
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(addVehicleForm.getRegistration());

        if (vehicleOptional.isPresent()) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("Vehicle already exists.");
        } else {
            Vehicle vehicle = new Vehicle();
            vehicle.setRegistration(addVehicleForm.getRegistration());
            vehicle.setGeoLocation(addVehicleForm.getGeoLocation());
            vehicle.setCategory(addVehicleForm.getCategory());

            vehicleRepository.insert(vehicle);

            genericResponse.setBody("Vehicle Added Successfully.");
        }

        return ResponseEntity.ok(genericResponse);
    }

    @PostMapping(path = "/vehicle/remove/{registration}")
    public ResponseEntity<GenericResponse> removeVehicle(@PathVariable("registration") String registration) {
        GenericResponse genericResponse = new GenericResponse();
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(registration);

        if (vehicleOptional.isEmpty()) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("Vehicle does not exist.");
        } else {
            vehicleRepository.deleteById(registration);
            genericResponse.setBody("Vehicle Removed Successfully.");
        }

        return ResponseEntity.ok(genericResponse);
    }
}
