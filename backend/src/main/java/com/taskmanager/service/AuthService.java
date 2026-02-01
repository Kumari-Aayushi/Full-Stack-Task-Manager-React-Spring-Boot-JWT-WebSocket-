package com.taskmanager.service;

import com.taskmanager.dto.AuthDtos;
import com.taskmanager.model.User;
import com.taskmanager.repo.UserRepository;
import com.taskmanager.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authManager;

    public void register(AuthDtos.RegisterRequest req) {
        if (userRepository.existsByEmail(req.email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(req.name)
                .email(req.email)
                .password(encoder.encode(req.password))
                .build();

        userRepository.save(user);
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email, req.password)
        );

        User user = userRepository.findByEmail(req.email).orElseThrow();

        String access = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refresh = jwtTokenProvider.generateRefreshToken(user.getEmail());

        return new AuthDtos.AuthResponse(
                access, refresh,
                new AuthDtos.UserDto(user.getId(), user.getName(), user.getEmail())
        );
    }

    public AuthDtos.AuthResponse refresh(AuthDtos.RefreshRequest req) {
        if (!jwtTokenProvider.validate(req.refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = jwtTokenProvider.getEmailFromToken(req.refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow();

        String access = jwtTokenProvider.generateAccessToken(email);
        String refresh = jwtTokenProvider.generateRefreshToken(email);

        return new AuthDtos.AuthResponse(
                access, refresh,
                new AuthDtos.UserDto(user.getId(), user.getName(), user.getEmail())
        );
    }
}
