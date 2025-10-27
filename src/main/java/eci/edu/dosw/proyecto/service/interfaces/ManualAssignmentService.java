package eci.edu.dosw.proyecto.service.interfaces;


import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.AssignmentStatus;
import org.springframework.stereotype.Service;

import java.util.List;
public interface ManualAssignmentService {
    ManualAssignmentResponse createManualAssignment(ManualAssignmentRequest assignmentRequest);
    ManualAssignmentResponse getManualAssignmentById(String id);
    List<ManualAssignmentResponse> getManualAssignmentsByStudent(String studentId);
    List<ManualAssignmentResponse> getManualAssignmentsByFaculty(String facultyId);
    ManualAssignmentResponse executeManualAssignment(String assignmentId);
    void cancelManualAssignment(String assignmentId);

    // Specific assignment types
    ManualAssignmentResponse assignStudentToSubject(ManualAssignmentRequest assignmentRequest);
    ManualAssignmentResponse assignStudentToGroup(ManualAssignmentRequest assignmentRequest);
    ManualAssignmentResponse withdrawStudentFromSubject(ManualAssignmentRequest assignmentRequest);
    ManualAssignmentResponse withdrawStudentFromGroup(ManualAssignmentRequest assignmentRequest);

    // Validation
    ManualAssignmentResponse validateManualAssignment(String assignmentId);
    boolean canOverridePrerequisites(String studentId, String subjectId);
    boolean canOverrideCapacity(String groupId);
    boolean canOverrideCreditLimit(String studentId);

    // Bulk operations
    List<ManualAssignmentResponse> getPendingManualAssignments();
    List<ManualAssignmentResponse> getManualAssignmentsByStatus(AssignmentStatus status);
}