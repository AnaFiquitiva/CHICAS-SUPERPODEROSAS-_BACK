package eci.edu.dosw.proyecto.dto;


import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de asignación o retiro de profesor.
 */
@Data
public class ProfessorAssignmentResponse {
    private String groupId;
    private String groupCode;
    private String subjectName;
    private String professorId;
    private String professorName;
    private String action;   // "ASSIGNED", "REPLACED", "REMOVED"
    private String message;
    private LocalDateTime actionDate;
}
