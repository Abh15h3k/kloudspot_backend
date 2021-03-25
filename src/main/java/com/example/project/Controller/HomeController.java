package com.example.project.Controller;

import com.example.project.Models.*;
import com.example.project.Models.Forms.RegisterForm;
import com.example.project.Models.Repository.JwtTokenRepository;
import com.example.project.Models.Repository.MyUserRepository;
import com.example.project.Util.JwtUtil;
import com.example.project.Util.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) throws Exception {

        JwtToken jwtToken = jwtTokenRepository.findById(authRequest.getUsername()).orElse(null);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException badCredentialsException) {
            jwtTokenRepository.delete(jwtToken);
            throw new Exception("Incorrect Username and Password", badCredentialsException);
        }

        if (jwtToken != null) {
            return ResponseEntity.ok(new AuthResponse(jwtToken.getToken()));
        }

        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        jwtTokenRepository.insert(new JwtToken(authRequest.getUsername(), jwt));

        return ResponseEntity.ok(new AuthResponse(jwt));
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
    public ResponseEntity<GenericResponse> register(@RequestBody RegisterForm registerForm) {
        GenericResponse genericResponse = new GenericResponse();
        // UserDetails myUserDetails =
        // myUserDetailsService.loadUserByUsername(registerForm.getEmailId());
        Optional<MyUser> myUserOptional = myUserRepository.findById(registerForm.getAadhar());

        if (myUserOptional.isPresent()) {
            genericResponse.setError(true);
            genericResponse.setErrorMessage("User already exists");
        } else {
            MyUser myUser = new MyUser(registerForm.getAadhar(), registerForm.getEmailId(), registerForm.getPassword(),
                    new DriverLicense(registerForm.getDriversLicense(), null), Arrays.asList(UserRole.USER),
                    AccountStatus.PROCESSING, null);
            myUserRepository.insert(myUser);
            genericResponse.setBody("User Successfully added");
        }
        return ResponseEntity.ok(genericResponse);
    }

}
