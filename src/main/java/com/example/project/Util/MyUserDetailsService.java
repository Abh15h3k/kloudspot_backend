package com.example.project.Util;

import com.example.project.Models.MyUser;
import com.example.project.Models.MyUserDetails;
import com.example.project.Models.Repository.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private MyUserRepository myUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyUser> userOptional = myUserRepository.findByEmailId(username);

        userOptional.orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        return userOptional.map(MyUserDetails::new).get();
    }
}
