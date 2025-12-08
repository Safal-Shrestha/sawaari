package com.sawari.dev.controller.auth;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
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
    public ResponseEntity<?> saveUser(@RequestBody Users user){
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok()
                .body("Signup Successful");
    }

    @PostMapping("/login")
    public ResponseEntity<?> generateToken(@RequestBody LoginUser loginUser) throws AuthenticationException {
        loginUser.getDeviceId();

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getUsername(),
                        loginUser.getPassword()
                )
        );

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtTokenUtil.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(principal.getId(), loginUser.getDeviceId());
        String role = principal.getAuthorities().iterator().next().getAuthority();

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/api/auth")
            .maxAge(60 * 60 * 24 * 7)
            .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of(
                    "accessToken", accessToken,
                    "role", role
                ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshTokenCookie, @RequestBody RefreshRequest request) {
        if (refreshTokenCookie == null) {
            return ResponseEntity.badRequest().body("Missing refresh token.");
        }

        RefreshToken storedToken = refreshTokenRepository.findByDeviceIdAndToken(request.getDeviceId(), refreshTokenCookie).orElse(null);

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

        RefreshToken newRefreshToken = refreshTokenService.rotateToken(storedToken);

        ResponseCookie newCookie = ResponseCookie.from("refreshToken", newRefreshToken.getToken())
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/api/auth")
            .maxAge(60 * 60 * 24 * 7)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, newCookie.toString())
            .body(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@CookieValue(value = "refreshToken", required = false) String refreshTokenCookie) {
        
        if (refreshTokenCookie == null) {
            return ResponseEntity.badRequest().body("Refresh token not found.");
        }

        refreshTokenRepository.findByToken(refreshTokenCookie)
            .ifPresent(refreshTokenRepository::delete);

        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/api/auth")
            .maxAge(0)
            .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body("Logged out successfully.");
    }
}