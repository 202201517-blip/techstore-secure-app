package com.techstoresecureapp.service;

import com.techstoresecureapp.dto.AuthResponse;
import com.techstoresecureapp.dto.LoginRequest;
import com.techstoresecureapp.dto.RegisterRequest;
import com.techstoresecureapp.entity.AppUser;
import com.techstoresecureapp.repository.AppUserRepository;
import com.techstoresecureapp.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (appUserRepository.existsByEmail(email)) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        AppUser user = new AppUser(
                request.getFullName(),
                email,
                passwordHash,
                "CUSTOMER"
        );

        AppUser savedUser = appUserRepository.save(user);

        return new AuthResponse(
                "Usuario registrado correctamente",
                savedUser.getEmail(),
                savedUser.getRole(),
                null
        );
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!user.getEnabled()) {
            throw new RuntimeException("El usuario se encuentra deshabilitado");
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                "Login correcto",
                user.getEmail(),
                user.getRole(),
                token
        );
    }
}