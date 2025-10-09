package eci.edu.dosw.proyecto.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "deans")
/*
 * Entidad que representa un Decano en el sistema
 */
public class Dean {
    @Id
    private String id;
    private String employeeCode;
    private String name;
    private String email;
    private String institutionalEmail;
    private String faculty; // Facultad a cargo
    private List<String> programs; // Programas bajo su responsabilidad
    private DeanType type;
    private Boolean active;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime lastLogin;

    // Permisos específicos
    private Boolean canApprovePlanChanges;
    private Boolean canViewAllRequests;
    private Boolean canManageFaculty;
    private Boolean canApproveSpecialRequests;
    private Boolean canGenerateFacultyReports;
}