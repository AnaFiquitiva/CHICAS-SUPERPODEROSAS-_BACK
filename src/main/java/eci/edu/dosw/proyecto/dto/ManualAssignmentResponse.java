package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.AssignmentStatus;
import eci.edu.dosw.proyecto.model.AssignmentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ManualAssignmentResponse {
    private String id;
    private AssignmentType type;
    private AssignmentStatus status;
    private String reason;
    private String justification;

    private StudentBasicResponse student;
    private SubjectBasicResponse subject;
    private GroupBasicResponse group;

    private boolean overridePrerequisites;
    private boolean overrideCapacity;
    private boolean overrideCreditLimit;
    private String overrideJustification;

    private String validationMessages;
    private String executionResult;

    private UserBasicResponse assignedBy;
    private LocalDateTime assignedAt;
    private LocalDateTime executedAt;
}
