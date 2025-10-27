package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupCapacityUpdateRequest {
    @NotNull(message = "El nuevo cupo es requerido")
    @Min(value = 1, message = "El cupo debe ser al menos 1")
    private Integer newCapacity;

    private String justification;
}
