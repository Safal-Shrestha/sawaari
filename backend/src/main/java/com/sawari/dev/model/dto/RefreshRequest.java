package com.sawari.dev.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequest {
    private Long userId;
    private String deviceId;
    private String refreshToken;
}