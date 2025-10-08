package eci.edu.dosw.proyecto.service.interfaces;
import eci.edu.dosw.proyecto.dto.*;

public interface ValidationService {

    void validateRequest(ChangeRequestRequestDTO requestDTO, String studentId);

    void validateStudentEligibility(String studentId);

    void validateGroupChange(ChangeRequestRequestDTO requestDTO, String studentId);

    void validateSubjectChange(ChangeRequestRequestDTO requestDTO, String studentId);

    void validatePlanChange(ChangeRequestRequestDTO requestDTO, String studentId);

    void validateNewEnrollment(ChangeRequestRequestDTO requestDTO, String studentId);
}