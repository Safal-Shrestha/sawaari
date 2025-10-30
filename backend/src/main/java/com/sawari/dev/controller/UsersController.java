package com.sawari.dev.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
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

    /*
    // POST: Example (for future use)
    @PostMapping("/userInfo")
    public User createUser(@RequestBody User newUser) {
        return userRepository.save(newUser);
    }
    */
}
