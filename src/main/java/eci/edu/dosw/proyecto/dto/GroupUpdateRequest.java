package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class GroupUpdateRequest {
    private Integer maxCapacity;
    private String professorId;
    private Boolean active;
}
