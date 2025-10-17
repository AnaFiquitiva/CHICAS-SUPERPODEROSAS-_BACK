package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.GroupAssignmentRequest;
import eci.edu.dosw.proyecto.dto.GroupAssignmentResponse;

import java.util.List;

public interface GroupAssignmentService {

    /**
     * Assigns a student to a specific group
     *
     * @param request Group assignment details
     * @return Assignment result with status
     */
    GroupAssignmentResponse assignStudentToGroup(GroupAssignmentRequest request);
    void bulkAssignStudents(List<GroupAssignmentRequest> requests);
    void removeStudentFromGroup(String enrollmentId);
}
