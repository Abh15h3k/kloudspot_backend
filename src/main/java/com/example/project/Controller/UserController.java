package com.example.project.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.example.project.Models.*;
import com.example.project.Models.Forms.UpdateProfileForm;
import com.example.project.Models.Repository.MyUserRepository;
import com.example.project.Models.Repository.TransactionRepository;
import com.example.project.Models.Repository.TripRepository;
import com.example.project.Models.Repository.VehicleRepository;
import com.example.project.Util.JwtUtil;

import com.example.project.Util.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "/user")
public class UserController {

    @Autowired private MyUserRepository myUserRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private TripRepository tripRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private Payment payment;

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

        if(myUser.getAccountStatus() == AccountStatus.PROCESSING) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("Your account is still being processed. Please try again later.");
            return ResponseEntity.ok(genericResponse);
        }

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

    @PostMapping(path = "/endtrip/{orderId}/{paymentId}")
    public ResponseEntity<GenericResponse> endTrip(
            @PathVariable(name = "orderId") String orderId,
            @PathVariable(name = "paymentId") String paymentId,
            HttpServletRequest httpServletRequest
    ) {
        GenericResponse genericResponse = new GenericResponse();
        String authHeader = httpServletRequest.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        MyUser myUser = myUserRepository.findByEmailId(username).orElse(null);

        System.out.println("OrderId: " + orderId + ", PaymentId: " + paymentId);

        if (myUser.getReservedVehicle() == null) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("You currently do not have any reserved vehicle.");
        } else {
            Transaction transaction = transactionRepository.findById(orderId).orElse(null);
            if(!payment.completed(orderId)) {
                System.out.println("!!!!!!!!!!!!!! PAYMENT NOT COMPLETED !!!!!!!!!!!!!!!");;
            }
            transaction.setPaymentId(paymentId);
            transaction.setStatus(TransactionStatus.PAID);
            transaction.setDatePaid(LocalDateTime.now());
            transactionRepository.save(transaction);

            Vehicle vehicle = vehicleRepository.findById(myUser.getReservedVehicle()).orElse(null);
            vehicle.setReservedBy(null);
            myUser.setReservedVehicle(null);

            Trip trip = myUser.getActiveTrip();
            System.out.println(trip);
            trip.setEndDateTime(LocalDateTime.now());

            myUser.setOrderId(null);
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

    @PostMapping(value = "/pay")
    public ResponseEntity<GenericResponse> pay(HttpServletRequest httpServletRequest) {
        GenericResponse genericResponse = new GenericResponse();

        String authHeader = httpServletRequest.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        MyUser myUser = myUserRepository.findByEmailId(username).orElse(null);

        String orderId = myUser.getOrderId();
        if(orderId != null) {
            genericResponse.setBody(orderId);
        } else {
            orderId = this.payment.createOrder(200); // amount in rupees
            myUser.setOrderId(orderId);
            Trip trip = myUser.getActiveTrip();
            Transaction transaction = new Transaction(orderId, null, trip.getTripId(), myUser.getAadhar(), myUser.getReservedVehicle(), 200.0, LocalDateTime.now(), null, TransactionStatus.INITIATED);
            this.transactionRepository.insert(transaction);
            this.myUserRepository.save(myUser);
            genericResponse.setBody(orderId);
        }

        return ResponseEntity.ok(genericResponse);
    }

//    @PostMapping(value = "/verifypayment/{orderId}")
//    public ResponseEntity<GenericResponse> verifyPayment(@PathVariable(name = "orderId") String orderId, HttpServletRequest httpServletRequest) {
//        GenericResponse genericResponse = new GenericResponse();
//        String authHeader = httpServletRequest.getHeader("Authorization");
//        String jwt = authHeader.substring(7);
//        String username = jwtUtil.extractUsername(jwt);
//        MyUser myUser = myUserRepository.findByEmailId(username).orElse(null);
//
//
//        genericResponse.setBody(payment.completed(orderId));
//
//        return ResponseEntity.ok(genericResponse);
//    }

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

    @GetMapping(path = "/getrecenttrips")
    public ResponseEntity<GenericResponse> getRecentTrips(HttpServletRequest httpServletRequest) {
        GenericResponse genericResponse = new GenericResponse();
        String authHeader = httpServletRequest.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        MyUser myUser = myUserRepository.findByEmailId(username).orElse(null);

        List<Trip> trips = tripRepository.findAllByUserAadhar(myUser.getAadhar());
        if(trips.size() > 5) {
            trips = trips.subList(0, 4);
        }

        genericResponse.setBody(trips);

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

    @PostMapping(path = "/updateprofile")
public ResponseEntity<GenericResponse> updateProfile(@RequestBody UpdateProfileForm updateProfileForm, @RequestParam(required = false) MultipartFile multipartFile, HttpServletRequest httpServletRequest) {
        GenericResponse genericResponse = new GenericResponse();
        String authHeader = httpServletRequest.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        MyUser myUser = myUserRepository.findByEmailId(username).orElse(null);

        System.out.println("Multipart File: " + !(multipartFile == null || multipartFile.isEmpty()));

        if(!myUser.getAadhar().equals(updateProfileForm.getAadhar())) {
            if(myUserRepository.findById(updateProfileForm.getAadhar()).isPresent()) {
                genericResponse.setError(true);
                genericResponse.setErrorMessage("Aadhar already exists.");
            }
        }

        if(!myUser.getEmailId().equals(updateProfileForm.getEmailId())) {
            if(myUserRepository.findByEmailId(updateProfileForm.getEmailId()) != null) {
                genericResponse.setError(true);
                genericResponse.setErrorMessage("EmailId already exists.");
            }
        }

        if(genericResponse.isError()) {
            return ResponseEntity.ok(genericResponse);
        }

        myUser.setFullName(updateProfileForm.getFullName());
        myUser.setAadhar(updateProfileForm.getAadhar());
        myUser.setEmailId(updateProfileForm.getEmailId());
        DriverLicense driverLicense = myUser.getDriverLicense();
        driverLicense.setLicenseNumber(updateProfileForm.getDriverLicenseNumber());
        myUser.setDriverLicense(driverLicense);

        myUserRepository.save(myUser);

        return ResponseEntity.ok(genericResponse);
    }


}
