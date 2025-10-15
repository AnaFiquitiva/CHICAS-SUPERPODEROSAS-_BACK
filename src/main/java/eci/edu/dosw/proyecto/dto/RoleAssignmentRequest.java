package eci.edu.dosw.proyecto.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleAssignmentRequest {
    private String userId;
    private String roleName; // Ejemplo: "PROFESSOR"
}
