package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.AdministratorType;
import lombok.Data;
import java.util.List;

@Data
public class AdministratorDTO {
    private String id;
    private String employeeCode;
    private String name;
    private String email;
    private String institutionalEmail;
    private String department;
    private String position;
    private AdministratorType type;
    private List<String> permissions;
    private Boolean active;

    // Permisos
    private Boolean canManageStudents;
    private Boolean canManageRequests;
    private Boolean canManageAcademicPeriods;
    private Boolean canGenerateReports;
    private Boolean canManageSystemConfig;
}