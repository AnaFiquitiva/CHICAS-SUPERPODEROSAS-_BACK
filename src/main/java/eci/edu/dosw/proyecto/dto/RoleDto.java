package eci.edu.dosw.proyecto.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto {
    private String id;
    private String name;
    private String description;
}
