package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.EnrollmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrollmentResponseDTO {
    private String id;
    private String studentId;
    private String subjectId;
    private String subjectName;
    private String groupId;
    private String groupCode;
    private LocalDateTime enrollmentDate;
    private EnrollmentStatus status;
    private String message;
}