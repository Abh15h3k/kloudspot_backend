package com.example.project.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.example.project.Models.DriverLicense;
import com.example.project.Models.GenericResponse;
import com.example.project.Models.MyUser;
import com.example.project.Models.Trip;
import com.example.project.Models.Vehicle;
import com.example.project.Models.Repository.MyUserRepository;
import com.example.project.Models.Repository.TripRepository;
import com.example.project.Models.Repository.VehicleRepository;
import com.example.project.Util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "/user")
public class UserController {

    @Autowired private MyUserRepository myUserRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private TripRepository tripRepository;

    @GetMapping(path = "")
    public ResponseEntity<GenericResponse> userCheck() {
        return ResponseEntity.ok(new GenericResponse(false, "", "Welcome User."));
    }

    @PostMapping(path = "/reserve/{registration}")
    public ResponseEntity<GenericResponse> reserveVehicle(@PathVariable("registration") String registration, HttpServletRequest httpServletRequest) {
        GenericResponse genericResponse = new GenericResponse();
        String authHeader = httpServletRequest.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        MyUser myUser = myUserRepository.findByEmailId(username).orElse(null);

        if (myUser.getReservedVehicle() != null) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("You have already reserved a vehicle. Please end the previous trip before reserving a new vehicle");
            return ResponseEntity.ok(genericResponse);
        }

        Optional<Vehicle> vOptional =  vehicleRepository.findById(registration);

        if (vOptional.isEmpty()) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("Vehicle does not exist.");
        } else {
            Vehicle vehicle = vOptional.get();

            myUser.setReservedVehicle(registration);
            vehicle.setReservedBy(myUser.getAadhar());

            Trip trip = new Trip();

            Long millis = System.currentTimeMillis();

            trip.setTripId(millis.toString());
            trip.setStartDateTime(LocalDateTime.now());
            trip.setUserAadhar(myUser.getAadhar());
            trip.setVehicleRegistration(registration);

            myUser.setActiveTrip(trip);

            myUserRepository.save(myUser);
            vehicleRepository.save(vehicle);
            tripRepository.insert(trip);

            genericResponse.setBody("vehicle successfully reserved.");
        }

        return ResponseEntity.ok(genericResponse);
    }

    @PostMapping(path = "/endtrip")
    public ResponseEntity<GenericResponse> endTrip(HttpServletRequest httpServletRequest) {
        GenericResponse genericResponse = new GenericResponse();
        String authHeader = httpServletRequest.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        MyUser myUser = myUserRepository.findByEmailId(username).orElse(null);

        if (myUser.getReservedVehicle() == null) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("You currently do not have any reserved vehicle.");
        } else {
            Vehicle vehicle = vehicleRepository.findById(myUser.getReservedVehicle()).orElse(null);
            vehicle.setReservedBy(null);
            myUser.setReservedVehicle(null);

            Trip trip = myUser.getActiveTrip();
            System.out.println(trip);
            trip.setEndDateTime(LocalDateTime.now());

            myUser.setActiveTrip(null);
            myUser.addTripId(trip.getTripId());
            vehicle.addTripId(trip.getTripId());

            tripRepository.save(trip);
            myUserRepository.save(myUser);
            vehicleRepository.save(vehicle);

            genericResponse.setBody("Trip ended successfully.");
        }

        return ResponseEntity.ok(genericResponse);
    }

    @PostMapping(path = "/uploaddl")
    public ResponseEntity<GenericResponse> uploadDl(
            @RequestParam("image")MultipartFile file,
            @RequestParam("adhaar")String adhaar) {
        GenericResponse genericResponse = new GenericResponse();

        if(adhaar.isEmpty()) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("adhaar cannot be empty.");
            return ResponseEntity.ok(genericResponse);
        }

        MyUser myUser = myUserRepository.findById(adhaar).orElse(null);
        if (myUser == null) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("customer with adhaar \"" + adhaar + "\" not found.");
            return ResponseEntity.ok(genericResponse);
        }

        try {
            DriverLicense driverLicense = myUser.getDriverLicense();
            driverLicense.setImageData(file.getBytes());
            System.out.println(file.getBytes().length);
            myUser.setDriverLicense(driverLicense);
        } catch (IOException ioException) {

        }

        myUserRepository.save(myUser);

        genericResponse.setBody("Uploaded successfully.");
       return ResponseEntity.ok(genericResponse);
    }

    @GetMapping(path = "/getdl/{adhaar}")
    public ResponseEntity<GenericResponse> getDl(
            @PathVariable("adhaar") String adhaar
    ) {
        GenericResponse genericResponse = new GenericResponse();
        MyUser myUser = myUserRepository.findById(adhaar).orElse(null);
        if (myUser == null) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("customer with adhaar \"" + adhaar + "\" not found.");
            return ResponseEntity.ok(genericResponse);
        }

        genericResponse.setBody(myUser.getDriverLicense().getImageData());
        return ResponseEntity.ok(genericResponse);
    }

    @GetMapping(path = "/gettriphistory")
    public ResponseEntity<GenericResponse> getTripHistroy(HttpServletRequest httpServletRequest) {
        GenericResponse genericResponse = new GenericResponse();
        String authHeader = httpServletRequest.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        MyUser myUser = myUserRepository.findByEmailId(username).orElse(null);

        List<Trip> trips = tripRepository.findAllByUserAadhar(myUser.getAadhar());

        genericResponse.setBody(trips);

        return ResponseEntity.ok(genericResponse);
    }

    @GetMapping(path = "/reservedvehicle")
    public ResponseEntity<GenericResponse> getReservedVehicle(HttpServletRequest httpServletRequest) {
        GenericResponse genericResponse = new GenericResponse();
        String authHeader = httpServletRequest.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        MyUser myUser = myUserRepository.findByEmailId(username).orElse(null);
        String reservedVehicleRegistration = myUser.getReservedVehicle();

        if (reservedVehicleRegistration == null || reservedVehicleRegistration.isEmpty()) {
            genericResponse.setBody(null);
        } else {
            Vehicle userVehicle = vehicleRepository.findById(reservedVehicleRegistration).orElse(null);
            genericResponse.setBody(userVehicle);
        }


        return ResponseEntity.ok(genericResponse);
    }

    @GetMapping(path = "/getprofile")
    public ResponseEntity<GenericResponse> getProfile(HttpServletRequest httpServletRequest) {
        GenericResponse genericResponse = new GenericResponse();
        String authHeader = httpServletRequest.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        MyUser myUser = myUserRepository.findByEmailId(username).orElse(null);

        genericResponse.setBody(myUser);
        return ResponseEntity.ok(genericResponse);
    }
}
