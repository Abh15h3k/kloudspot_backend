package com.example.project.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyUserDetails implements UserDetails {
    private String username;
    private String password;
    private AccountStatus accountStatus;
    private List<GrantedAuthority> authorities;

    public MyUserDetails(MyUser myUser) {
        this.username = myUser.getEmailId();
        this.password = myUser.getPassword();
        this.accountStatus = myUser.getAccountStatus();
        this.authorities = myUser.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return (this.accountStatus != AccountStatus.BLOCKED && this.accountStatus != AccountStatus.DL_BLOCKED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
//        return this.accountStatus == AccountStatus.ACTIVE;
    }

}
