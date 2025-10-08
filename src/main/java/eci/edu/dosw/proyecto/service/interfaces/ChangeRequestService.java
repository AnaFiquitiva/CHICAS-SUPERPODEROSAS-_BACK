package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.*;


import java.util.List;

public interface ChangeRequestService {

    ChangeRequestResponseDTO createChangeRequest(String studentId, ChangeRequestRequestDTO requestDTO);

    List<ChangeRequestResponseDTO> getStudentRequests(String studentId);

    ChangeRequestResponseDTO getRequestById(String requestId);

    ChangeRequestResponseDTO getRequestByNumber(String requestNumber);

    ChangeRequestResponseDTO cancelRequest(String requestId, String studentId);

    ValidationResponseDTO validateScheduleConflict(String studentId, String targetGroupId);

    ValidationResponseDTO validateAcademicPeriodActive();

    ValidationResponseDTO validateMaxRequests(String studentId);
}