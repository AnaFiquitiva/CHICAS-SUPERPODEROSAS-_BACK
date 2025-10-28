package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.BusinessValidationException;
import eci.edu.dosw.proyecto.exception.ForbiddenException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.UserService;
import eci.edu.dosw.proyecto.utils.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static eci.edu.dosw.proyecto.service.impl.GroupServiceImpl.getUser;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager; // Para autenticación real

    @Override
    @Transactional
    public UserResponse authenticate(LoginRequest loginRequest) {
        // Autenticación real con Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario", loginRequest.getUsername()));

        if (!user.isActive()) {
            throw new ForbiddenException("Usuario inactivo");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse changePassword(ChangePasswordRequest request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario", userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ForbiddenException("Contraseña actual incorrecta");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario", id));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario", username));
        return userMapper.toUserResponse(user);
    }

    @Override
    public List<UserResponse> getUsersByRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Rol", roleName));
        List<User> users = userRepository.findByRoleAndActiveTrue(role);
        return userMapper.toUserResponseList(users);
    }

    @Override
    @Transactional
    public UserResponse updateUser(String userId, UserResponse userResponse) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario", userId));

        user.setEmail(userResponse.getEmail());
        user.setActive(userResponse.isActive());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional
    public void deactivateUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario", userId));

        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public long countUsersByRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Rol", roleName));
        return userRepository.countByRoleAndActiveTrue(role);
    }

    @Override
    public User getCurrentAuthenticatedUser() {
        return getUser(userRepository);
    }
    // eci.edu.dosw.proyecto.service.impl.UserServiceImpl.java
    @Override
    @Transactional
    public UserResponse assignRole(RoleAssignmentRequest request) {
        User currentUser = getCurrentAuthenticatedUser();

        // Solo administradores pueden asignar roles
        if (!"ADMIN".equals(currentUser.getRole().getName())) {
            throw new ForbiddenException("Solo los administradores pueden asignar roles");
        }

        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("Usuario", request.getUserId()));

        Role newRole = roleRepository.findById(request.getNewRoleId())
                .orElseThrow(() -> new NotFoundException("Rol", request.getNewRoleId()));

        // Validar que no se elimine al último administrador
        if ("ADMIN".equals(targetUser.getRole().getName()) && !"ADMIN".equals(newRole.getName())) {
            long adminCount = userRepository.countByRoleAndActiveTrue(
                    roleRepository.findByName("ADMIN").orElseThrow()
            );
            if (adminCount <= 1) {
                throw new BusinessValidationException("No se puede eliminar el único administrador activo");
            }
        }

        targetUser.setRole(newRole);
        targetUser.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(targetUser);

        return userMapper.toUserResponse(savedUser);
    }
}