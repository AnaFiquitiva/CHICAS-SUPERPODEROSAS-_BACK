package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.Enrollment;

import java.util.List;

public interface EnrollmentService {

    EnrollmentResponseDTO enrollStudentToGroup(String studentId, EnrollmentRequestDTO enrollmentDTO);

    EnrollmentResponseDTO cancelEnrollment(String studentId, EnrollmentRequestDTO enrollmentDTO);

    List<EnrollmentResponseDTO> getStudentEnrollments(String studentId);

    ValidationResponseDTO validateGroupAvailability(String groupId);

    ValidationResponseDTO validateEnrollmentEligibility(String studentId, String groupId);
    List<Enrollment> getEnrollmentsByStudent(String studentId);
    List<Enrollment> getEnrollmentsByGroup(String groupId);
    Enrollment createEnrollment(Enrollment enrollment);
    void cancelEnrollment(String enrollmentId);
}