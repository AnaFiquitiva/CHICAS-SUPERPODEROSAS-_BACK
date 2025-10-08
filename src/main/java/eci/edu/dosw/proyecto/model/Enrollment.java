package eci.edu.dosw.proyecto.model;

import lombok.Data;

import java.time.LocalDateTime;
@Data

/*
 * Inscripción del estudiante en un grupo
 */
public class Enrollment {
    private String id;
    private String studentId;
    private String subjectId;
    private String groupId;
    private LocalDateTime enrollmentDate;
    private EnrollmentStatus status;

}