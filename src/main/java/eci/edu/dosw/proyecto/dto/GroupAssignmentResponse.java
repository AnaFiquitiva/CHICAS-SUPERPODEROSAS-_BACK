package eci.edu.dosw.proyecto.dto;

import lombok.Data;

/**
 * DTO for group assignment response
 * Contains result of group assignment operation
 */
@Data
public class GroupAssignmentResponse {
    private String id;
    private String studentId;
    private String studentName;
    private String groupId;
    private String groupCode;
    private String subjectName;
    private String status; // "ASSIGNED", "WAITLISTED", "REJECTED"
    private String message;
    private java.time.LocalDateTime assignmentDate;
    private Integer waitlistPosition; // If applicable
}
