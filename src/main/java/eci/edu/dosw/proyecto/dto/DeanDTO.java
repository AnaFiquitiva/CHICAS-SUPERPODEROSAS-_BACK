package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.DeanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class DeanDTO {

    @NotBlank(message = "El id  es obligatorio")
    private String id;

    @NotBlank(message = "El código de empleado es obligatorio")
    private String employeeCode;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El correo institucional es obligatorio")
    private String institutionalEmail;

    @NotBlank(message = "La facultad es obligatoria")
    private String faculty;

    @NotNull(message = "El tipo de decano es obligatorio")
    private DeanType type;

    private List<String> programs;

    // Permisos opcionales
    private Boolean canApprovePlanChanges;
    private Boolean canViewAllRequests;
    private Boolean canManageFaculty;
    private Boolean canApproveSpecialRequests;
    private Boolean canGenerateFacultyReports;
}