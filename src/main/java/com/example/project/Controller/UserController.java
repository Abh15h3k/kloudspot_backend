package com.example.project.Controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.example.project.Models.DriverLicense;
import com.example.project.Models.GenericResponse;
import com.example.project.Models.MyUser;
import com.example.project.Models.Vehicle;
import com.example.project.Models.Repository.MyUserRepository;
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

    @GetMapping(path = "")
    public ResponseEntity<?> userCheck() {
        return ResponseEntity.ok("Welcome User.");
    }

    @PostMapping(path = "/reserve/{registration}")
    public ResponseEntity<GenericResponse> reserveVehicle(@PathVariable("registration") String registration, HttpServletRequest httpServletRequest) {
        GenericResponse genericResponse = new GenericResponse();
        Optional<Vehicle> vOptional =  vehicleRepository.findById(registration);

        if (vOptional.isEmpty()) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("Vehicle does not exist.");
        } else {
            Vehicle vehicle = vOptional.get();
            String authHeader = httpServletRequest.getHeader("Authorization");
            String jwt = authHeader.substring(7);
            String username = jwtUtil.extractUsername(jwt);

            MyUser myUser = myUserRepository.findByEmailId(username).orElse(null);
            myUser.setReservedVehicle(registration);

            vehicle.setReservedBy(myUser.getAadhar());

            myUserRepository.save(myUser);
            vehicleRepository.save(vehicle);

            genericResponse.setBody("vehicle successfully reserved.");
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
}
