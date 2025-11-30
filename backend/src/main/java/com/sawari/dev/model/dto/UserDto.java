package com.sawari.dev.model.dto;

import java.sql.Date;

import com.sawari.dev.dbtypes.Gender;
import com.sawari.dev.dbtypes.UserRole;
import com.sawari.dev.model.Users;

public class UserDto {
    private Long userId;
    private String userName; 
    private String fullName;
    private Date dob; 
    private Long contact;
    private String country;
    private String email;
    private String password;
    private Gender gender;
    private UserRole role;

    public Users getUserFromDto(){
        Users user = new Users();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setDob(dob);
        user.setContact(contact);
        user.setCountry(country);
        user.setGender(gender);
        user.setRole(role);
        
        return user;
    } 
}
