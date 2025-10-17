/**
 * Service implementation for reassignment statistics reporting
 * Generates comprehensive reports on student reassignment activities
 */
package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.ChangeRequest;
import eci.edu.dosw.proyecto.model.RequestStatus;
import eci.edu.dosw.proyecto.model.RequestType;
import eci.edu.dosw.proyecto.repository.ChangeRequestRepository;
import eci.edu.dosw.proyecto.service.interfaces.ReassignmentReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReassignmentReportServiceImpl implements ReassignmentReportService {

    private final ChangeRequestRepository changeRequestRepository;

    @Override
    public ReassignmentReportResponse generateReassignmentReport(ReassignmentReportRequest request) {
        log.info("Generating reassignment report from {} to {}",
                request.getStartDate(), request.getEndDate());

        List<ChangeRequest> requests = filterRequests(request);

        ReassignmentReportResponse response = new ReassignmentReportResponse();
        response.setGlobal(calculateGlobalStatistics(requests));
        response.setByFaculty(calculateFacultyStatistics(requests));
        response.setBySubject(calculateSubjectStatistics(requests));
        response.setByGroup(calculateGroupStatistics(requests));
        response.setPeriod(createReportPeriod(request));

        log.info("Reassignment report generated successfully. Total requests analyzed: {}",
                requests.size());

        return response;
    }

    /**
     * Filters change requests based on request criteria
     * @param request Report request with filters
     * @return Filtered list of change requests
     */
    private List<ChangeRequest> filterRequests(ReassignmentReportRequest request) {
        // Implementation would filter by date range and optional criteria
        return changeRequestRepository.findByCreationDateBetween(
                request.getStartDate(), request.getEndDate());
    }

    /**
     * Calculates global statistics for all reassignment requests
     * @param requests List of change requests
     * @return Global statistics
     */
    private GlobalStatistics calculateGlobalStatistics(List<ChangeRequest> requests) {
        GlobalStatistics global = new GlobalStatistics();
        global.setTotalRequests(requests.size());

        long approved = requests.stream()
                .filter(request -> request.getStatus() == RequestStatus.APPROVED)
                .count();
        global.setApprovedRequests((int) approved);
        global.setApprovalRate(global.getTotalRequests() > 0 ?
                (double) approved / global.getTotalRequests() * 100 : 0.0);

        global.setGroupChanges((int) requests.stream()
                .filter(request -> request.getType() == RequestType.GROUP_CHANGE)
                .count());
        global.setSubjectChanges((int) requests.stream()
                .filter(request -> request.getType() == RequestType.SUBJECT_CHANGE)
                .count());
        global.setPlanChanges((int) requests.stream()
                .filter(request -> request.getType() == RequestType.PLAN_CHANGE)
                .count());

        return global;
    }

    /**
     * Calculates statistics grouped by faculty
     * @param requests List of change requests
     * @return Map of faculty statistics
     */
    private Map<String, FacultyStatistics> calculateFacultyStatistics(List<ChangeRequest> requests) {
        return requests.stream()
                .collect(Collectors.groupingBy(
                        this::extractFacultyFromRequest,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                this::calculateFacultyStats
                        )
                ));
    }

    /**
     * Extracts faculty information from a change request
     * @param request Change request
     * @return Faculty identifier
     */
    private String extractFacultyFromRequest(ChangeRequest request) {
        // Implementation would extract faculty from request data
        return "ENGINEERING"; // Placeholder
    }

    private FacultyStatistics calculateFacultyStats(List<ChangeRequest> requests) {
        FacultyStatistics stats = new FacultyStatistics();
        // Implementation would calculate faculty-specific statistics
        return stats;
    }

    private Map<String, SubjectStatistics> calculateSubjectStatistics(List<ChangeRequest> requests) {
        // Implementation for subject statistics
        return Map.of();
    }

    private Map<String, GroupStatistics> calculateGroupStatistics(List<ChangeRequest> requests) {
        // Implementation for group statistics
        return Map.of();
    }

    /**
     * Creates report period information
     * @param request Report request
     * @return Report period details
     */
    private ReportPeriod createReportPeriod(ReassignmentReportRequest request) {
        ReportPeriod period = new ReportPeriod();
        period.setStartDate(request.getStartDate().toString());
        period.setEndDate(request.getEndDate().toString());
        period.setAcademicPeriod(determineAcademicPeriod(request.getStartDate()));
        return period;
    }

    private String determineAcademicPeriod(LocalDateTime date) {
        // Implementation to determine academic period from date
        return "2024-1";
    }
}