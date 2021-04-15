package com.example.project.Controller;

import com.example.project.Models.*;
import com.example.project.Models.Forms.RegisterForm;
import com.example.project.Models.Repository.JwtTokenRepository;
import com.example.project.Models.Repository.MyUserRepository;
import com.example.project.Util.JwtUtil;
import com.example.project.Util.MyUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "/")
public class HomeController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private MyUserRepository myUserRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    @GetMapping(path = "/")
    public ResponseEntity<?> homeCheck() {
        return ResponseEntity.ok("this is da home page.");
    }

    @PostMapping(path = "/login")
    public ResponseEntity<GenericResponse> authenticate(@RequestBody AuthRequest authRequest) {
        GenericResponse genericResponse = new GenericResponse();
        JwtToken jwtToken = jwtTokenRepository.findById(authRequest.getUsername()).orElse(null);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException badCredentialsException) {
            if (jwtToken != null) {
                jwtTokenRepository.delete(jwtToken);
            }
            genericResponse.setError(true);
            genericResponse.setErrorMessage("Incorrect Login Credentials");
            return ResponseEntity.ok(genericResponse);
        }

        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authRequest.getUsername());

        if (jwtToken != null) {
            try {
                if (jwtUtil.validateToken(jwtToken.getToken(), userDetails)) {
                    genericResponse.setBody(jwtToken.getToken());
                    return ResponseEntity.ok(genericResponse);
                } else {
                    jwtTokenRepository.delete(jwtToken);
                }
            } catch (ExpiredJwtException e) {
                jwtTokenRepository.delete(jwtToken);
            }
        }

        final String jwt = jwtUtil.generateToken(userDetails);
        genericResponse.setBody(jwt);

        jwtTokenRepository.insert(new JwtToken(authRequest.getUsername(), jwt));

        return ResponseEntity.ok(genericResponse);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<GenericResponse> logout(HttpServletRequest httpServletRequest) {
        GenericResponse genericResponse = new GenericResponse();
        String authHeader = httpServletRequest.getHeader("Authorization");
        String jwt = authHeader.substring(7);
        String username = jwtUtil.extractUsername(jwt);

        jwtTokenRepository.deleteById(username);

        genericResponse.setBody("Successfully logged out.");
        return ResponseEntity.ok(genericResponse);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<GenericResponse> register(
            @RequestParam("aadhar") String aadhar,
            @RequestParam("fullName") String fullName,
            @RequestParam("emailId") String emailId,
            @RequestParam("password") String password,
            @RequestParam("driverLicense") String dlNumber,
            @RequestParam("image") MultipartFile dlImage
    ) {
        GenericResponse genericResponse = new GenericResponse();

        StringBuilder stringBuilder = new StringBuilder();

        // form validation
        if (aadhar.isEmpty() || aadhar.equals("undefined")) {
            stringBuilder.append("Adhaar cannot be empty.\n");
        }

        if (fullName.isEmpty() || fullName.equals("undefined")) {
            stringBuilder.append("Full Name cannot be empty.\n");
        }

        if (password.isEmpty() || password.equals("undefined")) {
            stringBuilder.append("Password cannot be empty.\n");
        }

        if (emailId.isEmpty() || emailId.equals("undefined")) {
            stringBuilder.append("Email Id cannot be empty.\n");
        }

        if (dlNumber.isEmpty() || dlNumber.equals("undefined")) {
            stringBuilder.append("DL Number cannot be empty.\n");
        }

        if (dlImage.isEmpty() || dlImage.equals("undefined")) {
            stringBuilder.append("please add picture of your dl.\n");
        }

        if (!stringBuilder.toString().isEmpty()) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage(stringBuilder.toString());
            return ResponseEntity.ok(genericResponse);
        }

        Optional<MyUser> myUserOptional = myUserRepository.findById(aadhar);
        Optional<MyUser> myUserOptional1 = myUserRepository.findByEmailId(emailId);

        if (myUserOptional.isPresent()) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("User with aadhar \"" + aadhar + "\" already exists");
        } else if (myUserOptional1.isPresent()) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("User with emailId \"" + emailId + "\" already exists");
        } else {

            MyUser myUser = new MyUser(aadhar, fullName, emailId, new BCryptPasswordEncoder().encode(password), new DriverLicense(dlNumber, null),
                    Arrays.asList(UserRole.USER), AccountStatus.PROCESSING, null, null, new ArrayList<>());
            try {
                DriverLicense driverLicense = myUser.getDriverLicense();
                driverLicense.setImageData(dlImage.getBytes());
                myUser.setDriverLicense(driverLicense);
            } catch (IOException ioException) {
                System.out.println("IOException while reading dlImage.\n" + ioException);
            }
            myUserRepository.insert(myUser);

//            final UserDetails userDetails = myUserDetailsService.loadUserByUsername(emailId);
//            final String jwt = jwtUtil.generateToken(userDetails);
//            genericResponse.setBody(jwt);
//            jwtTokenRepository.insert(new JwtToken(emailId, jwt));
        }
        return ResponseEntity.ok(genericResponse);
    }

    @PostMapping(path = "/verifyjwt")
    public ResponseEntity<GenericResponse> verifyJwt(@RequestParam("jwtToken") String jwtToken) {
        GenericResponse genericResponse = new GenericResponse();

        if (jwtToken.isEmpty() || jwtToken.equals("null")) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("Empty token received.");
            return ResponseEntity.ok(genericResponse);
        }

        try {
            String username = jwtUtil.extractUsername(jwtToken);
            if (jwtTokenRepository.findById(username).orElse(null) == null) {
                genericResponse.setBody(false);
            } else {
                genericResponse.setBody(true);
            }
        } catch (ExpiredJwtException e) {
            genericResponse.setBody(false);
        }
        return ResponseEntity.ok(genericResponse);
    }
}
