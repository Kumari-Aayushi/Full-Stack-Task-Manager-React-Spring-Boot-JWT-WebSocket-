package com.taskmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class AuthDtos {

    @Getter @Setter
    public static class RegisterRequest {
        @NotBlank public String name;
        @Email @NotBlank public String email;
        @NotBlank public String password;
    }

    @Getter @Setter
    public static class LoginRequest {
        @Email @NotBlank public String email;
        @NotBlank public String password;
    }

    @Getter @Setter @AllArgsConstructor
    public static class AuthResponse {
        public String accessToken;
        public String refreshToken;
        public UserDto user;
    }

    @Getter @Setter
    public static class RefreshRequest {
        @NotBlank public String refreshToken;
    }

    @Getter @Setter @AllArgsConstructor
    public static class UserDto {
        public Long id;
        public String name;
        public String email;
    }
}
