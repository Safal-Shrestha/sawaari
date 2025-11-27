package com.sawari.dev.model;

import java.sql.Date;

import com.sawari.dev.dbtypes.Gender;
import com.sawari.dev.dbtypes.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")

public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name", nullable = false)  // Maps to database column user_name
    private String userName;  // Java field name in camelCase
    
    @Column(name = "full_name")
    private String fullName;
    
    private Date dob; 
    private Long contact;
    private String country;
    private String email;   
    private String password;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
