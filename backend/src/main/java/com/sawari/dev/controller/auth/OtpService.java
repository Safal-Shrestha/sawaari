package com.sawari.dev.controller.auth;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OtpService {
    
    private final JavaMailSender mailSender;
    private final Map<String, OtpData> otpStorage = new HashMap<>();
    private final Map<String, Long> verifiedEmails = new HashMap<>(); 
    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALIDITY = 5 * 60 * 1000; 
    private static final long VERIFIED_VALIDITY = 10 * 60 * 1000; 

    public OtpService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void storeOtp(String email, String otp) {
        otpStorage.put(email, new OtpData(otp, System.currentTimeMillis()));
    }

    // Updated method that throws exception if email fails
    public void sendOtpEmail(String toEmail, String otp) throws RuntimeException {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("your-email@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Password Reset OTP");
            message.setText("Your OTP for password reset is: " + otp + 
                           "\n\nThis OTP is valid for 5 minutes." +
                           "\n\nIf you didn't request this, please ignore this email.");
            
            mailSender.send(message);
            
        } catch (MailException e) {
            System.err.println("Failed to send email to: " + toEmail);
            System.err.println("Error: " + e.getMessage());
            throw new RuntimeException("Failed to send OTP email. Please check your email address.");
        }
    }

    public boolean verifyOtp(String email, String otp) {
        OtpData otpData = otpStorage.get(email);
        
        if (otpData == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - otpData.timestamp > OTP_VALIDITY) {
            otpStorage.remove(email);
            return false;
        }

        if (otpData.otp.equals(otp)) {
            otpStorage.remove(email);
            return true;
        }

        return false;
    }

    public void markEmailAsVerified(String email) {
        verifiedEmails.put(email, System.currentTimeMillis());
    }

    public boolean isEmailVerified(String email) {
        Long verifiedTime = verifiedEmails.get(email);
        
        if (verifiedTime == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - verifiedTime > VERIFIED_VALIDITY) {
            verifiedEmails.remove(email);
            return false;
        }

        return true;
    }

    public void clearVerifiedEmail(String email) {
        verifiedEmails.remove(email);
    }

    // New method to remove OTP if email sending fails
    public void removeOtp(String email) {
        otpStorage.remove(email);
    }

    private static class OtpData {
        String otp;
        long timestamp;

        OtpData(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }
}