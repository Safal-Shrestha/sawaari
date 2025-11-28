package com.sawari.dev.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sawari.dev.model.RefreshToken;
import com.sawari.dev.repository.RefreshTokenRepository;
import com.sawari.dev.repository.UsersRepository;

@Service(value = "refreshToken")
public class RefreshTokenService {
    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsersRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository repo, UsersRepository userRepo) {
        this.refreshTokenRepository = repo;
        this.userRepository = userRepo;
    }

    public RefreshToken createRefreshToken(Long userId, String deviceId) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser_UserIdAndDeviceId(userId, deviceId);

        if (existingToken.isPresent()) {
            RefreshToken token = existingToken.get();
            System.out.println();
            if (isTokenExpired(token)) {
                refreshTokenRepository.delete(token);
            } else {
                return token;
            }
        }

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
        refreshToken.setDeviceId(deviceId);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken rotateToken(RefreshToken oldToken) {
        oldToken.setToken(UUID.randomUUID().toString());
        oldToken.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
        return refreshTokenRepository.save(oldToken);
        
    }

    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }
}
