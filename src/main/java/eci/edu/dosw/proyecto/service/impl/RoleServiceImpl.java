package eci.edu.dosw.proyecto.service;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final UserRepository userRepository;

    @Override
    public List<RoleDto> getAllRoles() {
        // Devuelve todos los roles definidos en el enum UserRole
        return Arrays.stream(UserRole.values())
                .map(role -> RoleDto.builder()
                        .id(role.name())  // Usamos el nombre del enum como "id"
                        .name(role.name())
                        .description("Rol del sistema: " + role.name())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public String assignRole(RoleAssignmentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserRole newRole;
        try {
            newRole = UserRole.valueOf(request.getRoleName().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rol no válido: " + request.getRoleName());
        }

        // Evita eliminar el único admin
        if (user.getRole() == UserRole.ADMIN && newRole != UserRole.ADMIN && !hasActiveAdmin()) {
            throw new RuntimeException("No puedes eliminar al único administrador activo");
        }

        user.setRole(newRole);
        userRepository.save(user);

        return "Rol " + newRole.name() + " asignado correctamente al usuario " + user.getUsername();
    }

    @Override
    public String transferAdminRole(String currentAdminId, String targetUserId) {
        User currentAdmin = userRepository.findById(currentAdminId)
                .orElseThrow(() -> new RuntimeException("Administrador actual no encontrado"));
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Usuario destino no encontrado"));

        currentAdmin.setRole(UserRole.PROFESSOR); // o el rol por defecto que definas
        targetUser.setRole(UserRole.ADMIN);

        userRepository.save(currentAdmin);
        userRepository.save(targetUser);

        return "Rol de administrador transferido correctamente a " + targetUser.getUsername();
    }

    @Override
    public boolean hasActiveAdmin() {
        return userRepository.findAll().stream()
                .anyMatch(user -> user.getRole() == UserRole.ADMIN);
    }

    // Este método ya no es necesario, pero lo dejamos por compatibilidad
    @Override
    public RoleDto createRole(RoleDto roleDto) {
        throw new UnsupportedOperationException("No se pueden crear roles dinámicamente, usa UserRole enum.");
    }
}
