package com.sawari.dev.Auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.model.Users;
import com.sawari.dev.repository.UsersRepository;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api")
public class ForgotPassword {
    
    private final UsersRepository usersRepository;
    private final OtpService otpService;

    public ForgotPassword(UsersRepository usersRepository, OtpService otpService) {
        this.usersRepository = usersRepository;
        this.otpService = otpService;
    }

    @PostMapping("/ForgotPassword")
    public ResponseEntity<String> sendOtpAfterValidation(@RequestBody Users forgetUsers) {
        try {
            if (forgetUsers.getEmail() == null || forgetUsers.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }

            
            Users user = usersRepository.findByEmail(forgetUsers.getEmail());

            if (user != null) {
        
                String otp = otpService.generateOtp();
                
            
                otpService.storeOtp(user.getEmail(), otp);
                
                
                otpService.sendOtpEmail(user.getEmail(), otp);
                
                return ResponseEntity.ok("OTP sent successfully to your email");
            } else {
                return ResponseEntity.status(404).body("Email not found");
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @PostMapping("/VerifyOtp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpRequest otpRequest) {
        try {
            
            if (otpRequest.getEmail() == null || otpRequest.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }
            if (otpRequest.getOtp() == null || otpRequest.getOtp().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("OTP is required");
            }

            // otp validate garna 
            boolean isValid = otpService.verifyOtp(otpRequest.getEmail(), otpRequest.getOtp());
            
            if (isValid) {
                // email verify garna password change garna ko lagi 
                otpService.markEmailAsVerified(otpRequest.getEmail());
                return ResponseEntity.ok("OTP verified successfully. You can now reset your password.");
            } else {
                return ResponseEntity.status(400).body("Invalid or expired OTP");
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @PostMapping("/ResetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetRequest) {
        try {
        
            if (resetRequest.getEmail() == null || resetRequest.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }
            if (resetRequest.getNewPassword() == null || resetRequest.getNewPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("New password is required");
            }
            if (resetRequest.getConfirmPassword() == null || resetRequest.getConfirmPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Confirm password is required");
            }

        
           

             String password = resetRequest.getNewPassword();
            if (!password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
                return ResponseEntity.badRequest()
                        .body("Password must be 8+ chars, contain uppercase, number and special char");
            }

            // Check if email was verified with OTP
            if (!otpService.isEmailVerified(resetRequest.getEmail())) {
                return ResponseEntity.status(403).body("Please verify OTP first before resetting password");
            }

        
            Users user = usersRepository.findByEmail(resetRequest.getEmail());
            if (user == null) {
                return ResponseEntity.status(404).body("User not found");
            }

            
            user.setPassword(resetRequest.getNewPassword());
            usersRepository.save(user);

            // Clear verified email status after successful password reset
            otpService.clearVerifiedEmail(resetRequest.getEmail());

            return ResponseEntity.ok("Password reset successfully");
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    // DTO class for OTP verification 
    public static class OtpRequest {
        private String email;
        private String otp;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }
    }

    // DTO class for password reset
    public static class ResetPasswordRequest {
        private String email;
        private String newPassword;
        private String confirmPassword;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
}