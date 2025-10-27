package eci.edu.dosw.proyecto.service.interfaces;


import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.RequestStatus;
import org.springframework.stereotype.Service;

import java.util.List;
public interface RequestService {
    // Basic CRUD
    RequestResponse createRequest(RequestCreateRequest requestCreateRequest);
    RequestResponse getRequestById(String id);
    RequestResponse getRequestByNumber(String requestNumber);
    List<RequestResponse> getRequestsByStudent(String studentId);
    List<RequestResponse> getRequestsByStatus(RequestStatus status);
    List<RequestResponse> getAllRequests();
    RequestResponse updateRequest(String requestId, RequestUpdateRequest requestUpdateRequest);
    void cancelRequest(String requestId, String studentId);

    // Faculty-specific operations
    List<RequestResponse> getRequestsByFaculty(String facultyId);
    List<RequestResponse> getRequestsByFacultyAndStatus(String facultyId, RequestStatus status);
    List<RequestBasicResponse> getFacultyRequestsByPriority(String facultyId);
    List<RequestBasicResponse> getFacultyRequestsByArrival(String facultyId);

    // Decision making
    RequestResponse approveRequest(String requestId, RequestDecisionRequest decisionRequest, String processedBy);
    RequestResponse rejectRequest(String requestId, RequestDecisionRequest decisionRequest, String processedBy);
    RequestResponse requestAdditionalInfo(String requestId, RequestDecisionRequest decisionRequest, String processedBy);
    RequestResponse processSpecialApproval(String requestId, RequestDecisionRequest decisionRequest, String processedBy);

    // Validation and checks
    boolean validateRequest(RequestCreateRequest requestCreateRequest);
    boolean hasScheduleConflict(String studentId, String groupId);
    boolean meetsAcademicRequirements(String studentId, String subjectId);

    // Bulk operations
    List<RequestResponse> getPendingRequestsByFaculty(String facultyId);
    List<RequestBasicResponse> getGlobalRequestsByPriority();

    // History
    List<RequestHistoryResponse> getRequestHistory(String requestId);

    // Statistics
    long countRequestsByStatusAndFaculty(RequestStatus status, String facultyId);
    RequestStatsResponse getRequestStatistics(String facultyId);
}