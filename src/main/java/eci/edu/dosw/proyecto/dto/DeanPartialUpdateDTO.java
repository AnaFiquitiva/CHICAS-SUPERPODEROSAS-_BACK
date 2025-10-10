package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO para actualización parcial de un Decano (PATCH).
 * Solo se permiten actualizar algunos campos.
 */
@Data
public class DeanPartialUpdateDTO {
    private String institutionalEmail;
    private List<String> programs;
    private Boolean canApprovePlanChanges;
    private Boolean canViewAllRequests;
    private Boolean canManageFaculty;
    private Boolean canApproveSpecialRequests;
    private Boolean canGenerateFacultyReports;
}