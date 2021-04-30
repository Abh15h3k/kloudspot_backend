package com.example.project.Controller;

import java.util.List;
import java.util.Optional;

import com.example.project.Models.Forms.ModifyVehicleForm;
import com.example.project.Models.GenericResponse;
import com.example.project.Models.MyUser;
import com.example.project.Models.Trip;
import com.example.project.Models.Vehicle;
import com.example.project.Models.Forms.AddVehicleForm;
import com.example.project.Models.Forms.UpdateAccountStatusForm;
import com.example.project.Models.Repository.MyUserRepository;
import com.example.project.Models.Repository.TripRepository;
import com.example.project.Models.Repository.VehicleRepository;

import com.example.project.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "/admin")
public class AdminController {

    @Autowired
    private MyUserRepository myUserRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired private JwtUtil jwtUtil;

    @GetMapping(path = "/")
    public ResponseEntity<GenericResponse> adminCheck() {
        return ResponseEntity.ok(new GenericResponse(false, "", "Welcome Admin."));
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

    @PostMapping(path = "/vehicle/modify")
    public ResponseEntity<GenericResponse> modifyVehicle(@RequestBody ModifyVehicleForm modifyVehicleForm) {
        GenericResponse genericResponse = new GenericResponse();
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(modifyVehicleForm.getRegistration());

        if(vehicleOptional.isEmpty()) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("Vehicle does not exist.");
        } else {
            Vehicle vehicle = vehicleOptional.get();
            vehicle.setCategory(modifyVehicleForm.getCategory());
            vehicle.setMake(modifyVehicleForm.getMake());
            vehicle.setModel(modifyVehicleForm.getModel());
            vehicleRepository.save(vehicle);
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
        } else if(vehicleOptional.get().getReservedBy() != null) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("Vehicle is in Use.");
        } else {
            vehicleRepository.deleteById(registration);
            genericResponse.setBody("Vehicle Removed Successfully.");
        }

        return ResponseEntity.ok(genericResponse);
    }

    @GetMapping(path = "/trips/vehicle/{registration}")
    public ResponseEntity<GenericResponse> getTripsForVehicle(@PathVariable("registration") String registration) {
        GenericResponse genericResponse = new GenericResponse();
        List<Trip> trips = tripRepository.findAllByVehicleRegistration(registration);

        genericResponse.setBody(trips);

        return ResponseEntity.ok(genericResponse);
    }

    @GetMapping(path = "/trips/user/{aadhar}")
    public ResponseEntity<GenericResponse> getTripsForUser(@PathVariable("aadhar") String aadhar) {
        GenericResponse genericResponse = new GenericResponse();
        List<Trip> trips = tripRepository.findAllByUserAadhar(aadhar);

        genericResponse.setBody(trips);

        return ResponseEntity.ok(genericResponse);
    }

    @GetMapping(path = "/getusers")
    public ResponseEntity<GenericResponse> getUsers(HttpServletRequest httpServletRequest) {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        MyUser myUser = myUserRepository.findByEmailId(username).orElse(null);

        List<MyUser> users = myUserRepository.findAll();
        users.removeIf((user) -> { return user.getAadhar().equals(myUser.getAadhar()); });

        return ResponseEntity.ok(new GenericResponse(false, "", users));
    }

    @GetMapping(path = "/getusercount")
    public ResponseEntity<GenericResponse> getUserCount() {
        return ResponseEntity.ok(new GenericResponse(false, "", myUserRepository.count()));
    }

    @GetMapping(path = "/getvehiclecount")
    public ResponseEntity<GenericResponse> getVehicleCount() {
        return ResponseEntity.ok(new GenericResponse(false, "" , vehicleRepository.count()));
    }

    @GetMapping(path = "/gettripcount")
    public ResponseEntity<GenericResponse> getTripCount() {
        return ResponseEntity.ok(new GenericResponse(false, "" , tripRepository.count()));
    }
}
