package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.*;

import java.util.List;

public interface EnrollmentService {

    EnrollmentResponseDTO enrollStudentToGroup(String studentId, EnrollmentRequestDTO enrollmentDTO);

    EnrollmentResponseDTO cancelEnrollment(String studentId, EnrollmentRequestDTO enrollmentDTO);

    List<EnrollmentResponseDTO> getStudentEnrollments(String studentId);

    ValidationResponseDTO validateGroupAvailability(String groupId);

    ValidationResponseDTO validateEnrollmentEligibility(String studentId, String groupId);
}