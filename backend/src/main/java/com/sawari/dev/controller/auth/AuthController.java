package com.sawari.dev.controller.auth;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.jwtimpl.TokenProvider;
import com.sawari.dev.model.RefreshToken;
import com.sawari.dev.model.Users;
import com.sawari.dev.model.dto.LoginUser;
import com.sawari.dev.model.dto.RefreshRequest;
import com.sawari.dev.repository.RefreshTokenRepository;
import com.sawari.dev.repository.UsersRepository;
import com.sawari.dev.service.CustomUserDetails;
import com.sawari.dev.service.RefreshTokenService;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    RefreshTokenService refreshTokenService;

    @GetMapping("/userInfo")
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/signup")
    public Users saveUser(@RequestBody Users user){
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @PostMapping("/login")
    public Map<String, String> generateToken(@RequestBody LoginUser loginUser) throws AuthenticationException {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getUsername(),
                        loginUser.getPassword()
                )
        );

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtTokenUtil.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(principal.getId(), loginUser.getDeviceId());

        return Map.of(
            "accessToken", accessToken,
            "refreshToken", refreshToken.getToken()
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByUser_UserIdAndDeviceId(request.getUserId(), request.getDeviceId()).orElse(null);

        if (storedToken == null) {
            return ResponseEntity.badRequest().body("Invalid refresh token.");
        }

        // Check expiration
        if(refreshTokenService.isTokenExpired(storedToken)) {
            refreshTokenRepository.delete(storedToken);
            return ResponseEntity.badRequest().body("Refresh token expired. Please login again.");
        }

        Users user = storedToken.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities());

        String newAccessToken = jwtTokenUtil.generateToken(authentication);

        refreshTokenRepository.delete(storedToken);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getUserId(), request.getDeviceId());

        return ResponseEntity.ok(
            Map.of(
                    "accessToken", newAccessToken,
                    "refreshToken", newRefreshToken.getToken()
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody Map<String, String> payload) {
        String requestToken = payload.get("refreshToken");

        if (requestToken == null || requestToken.isBlank()) {
            return ResponseEntity.badRequest().body("Refresh token is required.");
        }

        return refreshTokenRepository.findByToken(requestToken)
                .map(token -> {
                    refreshTokenRepository.delete(token);
                    return ResponseEntity.ok("Logged out successfully.");
                })
                .orElse(ResponseEntity.badRequest().body("Invalid refresh token."));
    }
}