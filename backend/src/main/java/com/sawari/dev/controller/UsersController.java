package com.sawari.dev.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.model.Users;
import com.sawari.dev.repository.UsersRepository;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class UsersController {

    private final UsersRepository userRepository;

    // Dependency Injection (constructor-based)
    public UsersController(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET: Fetch all users
    @GetMapping("/userInfo")
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    
    @PostMapping("/userInfo")
public ResponseEntity<String> createUser(@RequestBody Users newUser) {

    try {

       if (newUser.getFullName() == null || newUser.getFullName().trim().isEmpty()) {// yo chai field lai required garna and leading and ending space lai cancle garna 
            return ResponseEntity.badRequest().body("Full name is required");
        }
        if (newUser.getUserName() == null || newUser.getUserName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username is required");
        }
        if (newUser.getDob() == null) {
            return ResponseEntity.badRequest().body("Date of birth is required");
        }
        if (newUser.getGender() == null || newUser.getGender().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Gender is required");
        }
        if (newUser.getContact() == null) {
            return ResponseEntity.badRequest().body("Contact is required");
        }
        if (newUser.getCountry() == null || newUser.getCountry().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Country is required");
        }
        if (newUser.getRole() == null || newUser.getRole().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Role is required");
        }
        if (newUser.getEmail() == null || newUser.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (newUser.getPassword() == null || newUser.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }


        // Contact validation
        String contactStr = String.valueOf(newUser.getContact());
        if (!contactStr.matches("^(97|98)\\d{8}$")) {
            return ResponseEntity.badRequest().body("Contact must start with 97 or 98 and be 10 digits");
        }

        // Check unique username
        if (userRepository.existsByUserName(newUser.getUserName())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        // Check unique email
        if (userRepository.existsByEmail(newUser.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
      
      

        // Password validation
        String password = newUser.getPassword();
        if (!password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
            return ResponseEntity.badRequest()
                    .body("Password must be 8+ chars, contain uppercase, number and special char");
        }

        userRepository.save(newUser);

        return ResponseEntity.ok("success");

    } catch (Exception e) {
        return ResponseEntity.status(500).body("Server error: " + e.getMessage());
    }
}

     
    
}
