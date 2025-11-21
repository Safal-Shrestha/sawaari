package com.sawari.dev.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.model.Users;
import com.sawari.dev.repository.UsersRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class LoginController {
    private final UsersRepository userRepository;

    public LoginController(UsersRepository usersRepository){
        this.userRepository = usersRepository;
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> checkUserForLogin(@RequestBody Users loginUser) {
         
        try {
       
            if (loginUser.getEmail() == null || loginUser.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }
            
        
            if (loginUser.getPassword() == null || loginUser.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required");
            }
            
            
            Users user = userRepository.findByEmail(loginUser.getEmail());
       
            if (user != null && EncryptinAndDecryption.decrypt(user.getPassword()).equals(loginUser.getPassword())) {
                // Login successful
                return ResponseEntity.ok("success");
            } 
            else if (user == null) {
                // if email wrong bhayo bhane 
                return ResponseEntity.badRequest().body("Email not found");
            } 
            else {
                // Email  cha tara password bigriyo bhane 
                return ResponseEntity.badRequest().body("Invalid password");// yo bad req cha return matri use garo bhane js la lidaina tai bhayera http response  pathaunu parcha 
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }
}