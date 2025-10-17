/**
 * DTO for group assignment request
 * Contains information needed to assign a student to a specific group
 */
package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class GroupAssignmentRequest {
    @NotNull(message = "Student ID is required")
    private String studentId;

    @NotNull(message = "Group ID is required")
    private String groupId;

    private String reason;
    private Boolean forceAssignment = false; // For overcapacity scenarios
    private String assignedBy; // Administrator performing the assignment
}

