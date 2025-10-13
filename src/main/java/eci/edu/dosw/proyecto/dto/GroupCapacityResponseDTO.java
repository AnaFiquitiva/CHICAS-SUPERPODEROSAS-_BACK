package eci.edu.dosw.proyecto.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa la respuesta del cupo actual de un grupo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupCapacityResponseDTO {
    private String groupId;
    private Integer enrolledCount;
    private Integer maxCapacity;
    private Double occupancyPercentage;
}
