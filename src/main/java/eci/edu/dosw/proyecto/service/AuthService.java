package eci.edu.dosw.proyecto.service;

import eci.edu.dosw.proyecto.dto.LoginRequest;
import eci.edu.dosw.proyecto.dto.LoginResponse;
import eci.edu.dosw.proyecto.dto.RegisterRequest;
import eci.edu.dosw.proyecto.model.User;
import eci.edu.dosw.proyecto.model.UserRole;
import eci.edu.dosw.proyecto.repository.UserRepository;
import eci.edu.dosw.proyecto.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // --- REGISTRO DE USUARIO ---
    public String register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        // Convertir el texto del rol a un valor del enum UserRole
        UserRole role;
        try {
            role = UserRole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rol inválido: " + request.getRole());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        userRepository.save(user);
        return "Usuario registrado exitosamente con rol: " + role;
    }

    // --- LOGIN ---
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());

        return new LoginResponse(token, user.getUsername(), user.getRole().name());
    }
}


