package eci.edu.dosw.proyecto.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleTransferRequest {
    private String currentAdminId;
    private String targetUserId;
}
