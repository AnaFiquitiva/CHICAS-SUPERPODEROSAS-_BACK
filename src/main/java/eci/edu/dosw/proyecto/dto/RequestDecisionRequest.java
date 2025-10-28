package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.RequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestDecisionRequest {
    @NotNull(message = "Estado es requerido")
    private RequestStatus status;

    private String justification; // Obligatorio para rechazo
    private String comments;

    private Boolean specialApproval; // Para aprobación especial
    private String specialApprovalJustification;
}
