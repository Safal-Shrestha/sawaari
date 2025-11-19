package com.sawari.dev.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.model.Users;
import com.sawari.dev.repository.UsersRepository;

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
    public String  createUser(@RequestBody Users newUser) {
        if (newUser.getUserName() == null || newUser.getUserName().trim().isEmpty()) {
        return "Username is required";
    }
    if (newUser.getFullName() == null || newUser.getFullName().trim().isEmpty()) {//.trimp la starting or ending ko space lai hataucha 

        return "Full name is required";
    }
    if (newUser.getEmail() == null || newUser.getEmail().trim().isEmpty()) {
        return "Email is required";
    }
    if (newUser.getPassword() == null || newUser.getPassword().trim().isEmpty()) {
        return "Password is required";
    }

    //  contact validation 
    String contactstr=String.valueOf(newUser.getContact());
    if (!contactstr.matches("(97|98\\d{8}$)")){
        return "contact must start with 97 or 98 and be 10 digits";

    }


    // // unique username 
    // if (userRepository.existsByUserName(newUser.getUserName()))
    // {
    //     return "Username already exists";

    // }

    // // check email
    // if(userRepository.existsBYEmail(newUser.getEmail())){
    //     return "Email already exists";

    // }

    // for password restrictions 
    String password=newUser.getPassword();
    if (!password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
    return "Password must be at least 8 characters and contain at least one uppercase letter, one number, and one special character (@, #, !, etc.)";
}
     


     userRepository.save(newUser);
         return "User register successfully";
    }
     
    
}