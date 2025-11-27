package com.sawari.dev.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sawari.dev.dbtypes.UserRole;
import com.sawari.dev.model.Users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private UserRole role;

    public CustomUserDetails(Users user) {
        this.id = user.getUserId();
        this.username = user.getUserName();
        this.password = user.getPassword();
        this.role = user.getRole();
    }

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        authorities.add(new SimpleGrantedAuthority(role.name()));
        return authorities;
    }
}
