package eci.edu.dosw.proyecto.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "administrators")
/*
 * Entidad que representa un Administrador en el sistema
 */
public class Administrator {
    @Id
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
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime lastLogin;

    // Permisos específicos
    private Boolean canManageStudents;
    private Boolean canManageRequests;
    private Boolean canManageAcademicPeriods;
    private Boolean canGenerateReports;
    private Boolean canManageSystemConfig;
}