package com.example.project.Controller;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.example.project.Models.*;
import com.example.project.Models.Forms.ModifyVehicleForm;
import com.example.project.Models.Forms.AddVehicleForm;
import com.example.project.Models.Forms.UpdateAccountStatusForm;
import com.example.project.Models.Forms.UpdateAccountsStatusForm;
import com.example.project.Models.Repository.MyUserRepository;
import com.example.project.Models.Repository.TripRepository;
import com.example.project.Models.Repository.VehicleRepository;

import com.example.project.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    final String Digits     = "(\\p{Digit}+)";
    final String HexDigits  = "(\\p{XDigit}+)";
    // an exponent is 'e' or 'E' followed by an optionally
// signed decimal integer.
    final String Exp        = "[eE][+-]?"+Digits;
    final String fpRegex    =
            ("[\\x00-\\x20]*"+ // Optional leading "whitespace"
                    "[+-]?(" +         // Optional sign character
                    "NaN|" +           // "NaN" string
                    "Infinity|" +      // "Infinity" string

                    // A decimal floating-point string representing a finite positive
                    // number without a leading sign has at most five basic pieces:
                    // Digits . Digits ExponentPart FloatTypeSuffix
                    //
                    // Since this method allows integer-only strings as input
                    // in addition to strings of floating-point literals, the
                    // two sub-patterns below are simplifications of the grammar
                    // productions from the Java Language Specification, 2nd
                    // edition, section 3.10.2.

                    // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                    "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

                    // . Digits ExponentPart_opt FloatTypeSuffix_opt
                    "(\\.("+Digits+")("+Exp+")?)|"+

                    // Hexadecimal strings
                    "((" +
                    // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HexDigits + "(\\.)?)|" +

                    // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                    ")[pP][+-]?" + Digits + "))" +
                    "[fFdD]?))" +
                    "[\\x00-\\x20]*");// Optional trailing "whitespace"

    @GetMapping(path = "/")
    public ResponseEntity<GenericResponse> adminCheck() {
        return ResponseEntity.ok(new GenericResponse(false, "", "Welcome Admin."));
    }

    @PostMapping(path = "/updateaccounts")
    public ResponseEntity<GenericResponse> updateAccounts(@RequestBody UpdateAccountsStatusForm updateAccountsStatusForm) {
        GenericResponse genericResponse = new GenericResponse();
        boolean minSuccess = false;
//        String[]  aadhars = updateAccountsStatusForm.getAadhar();
//        AccountStatus[]  statuses = updateAccountsStatusForm.getStatus();
        MyUser[] updatedUsers = updateAccountsStatusForm.getUsers();
        for(int i = 0; i < updatedUsers.length; ++i) {
            Optional<MyUser> myUserOptional = myUserRepository.findById(updatedUsers[i].getAadhar());
            if (myUserOptional.isPresent()) {
                MyUser myUser = myUserOptional.get();
                System.out.print(myUser.getAadhar() + " Before: " + myUser.getAccountStatus());
                myUser.setAccountStatus(updatedUsers[i].getAccountStatus());
                System.out.println(" After: " + myUser.getAccountStatus());
                myUserRepository.save(myUser);
                minSuccess = true;
            }
        }

        if(!minSuccess) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("none of the users were found.");
        } else {
            genericResponse.setBody("Users updated successfully.");
        }
        return ResponseEntity.ok(genericResponse);
    }

    @PostMapping(path = "/updateaccount")
    public ResponseEntity<GenericResponse> updateAccount(@RequestBody UpdateAccountStatusForm updateAccountStatusForm) {
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
            vehicle.setMake(addVehicleForm.getMake());
            vehicle.setModel(addVehicleForm.getModel());

            vehicleRepository.insert(vehicle);

            genericResponse.setBody("Vehicle Added Successfully.");
        }

        return ResponseEntity.ok(genericResponse);
    }

    @PostMapping(path = "/vehicle/bulkAdd")
    public ResponseEntity<GenericResponse> bulkAddVehicles(
            @RequestParam("multipartFile") MultipartFile multipartFile
    ) {
        GenericResponse genericResponse = new GenericResponse();
        int totalVehicles = 0;
        int vehiclesAdded = 0;
        try {
            InputStream inputStream = multipartFile.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if(line.startsWith("#")) {
                    continue;
                }
                ++totalVehicles;

                String[] tokens = line.split(",");
                if(tokens.length != 6) {
                    continue;
                }

                if(vehicleRepository.findById(tokens[0]).isPresent()) {
                    continue;
                }

                if(!Pattern.matches(fpRegex, tokens[1]) || !Pattern.matches(fpRegex, tokens[2])) {
                    continue;
                }

                boolean validEnum = false;
                for(VehicleCategory cat : VehicleCategory.values()) {
                    if(tokens[3].equals(cat.name())) {
                        validEnum = true;
                        break;
                    }
                }

                if(!validEnum) {
                    continue;
                }

                GeoLocation geoLocation = new GeoLocation(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]));

                Vehicle vehicle = new Vehicle();
                vehicle.setRegistration(tokens[0]);
                vehicle.setGeoLocation(geoLocation);
                vehicle.setCategory(VehicleCategory.valueOf(tokens[3]));
                vehicle.setMake(tokens[4]);
                vehicle.setModel(tokens[5]);

                this.vehicleRepository.insert(vehicle);
                ++vehiclesAdded;
            }
            genericResponse.setBody("Number of vehicles received: " + totalVehicles + "\nNumber of vehicles added: " + vehiclesAdded);
        } catch (IOException ioException) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("Server Encountered an error reading the file. " + ioException.getMessage());
            return  ResponseEntity.ok(genericResponse);
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
