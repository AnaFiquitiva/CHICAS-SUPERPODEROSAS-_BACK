package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.DeanType;
import lombok.Data;
import java.util.List;

@Data
public class DeanDTO {
    private String id;
    private String employeeCode;
    private String name;
    private String email;
    private String institutionalEmail;
    private String faculty;
    private List<String> programs;
    private DeanType type;
    private Boolean active;

    // Permisos
    private Boolean canApprovePlanChanges;
    private Boolean canViewAllRequests;
    private Boolean canManageFaculty;
    private Boolean canApproveSpecialRequests;
    private Boolean canGenerateFacultyReports;
}