package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.AcademicPeriod;
import eci.edu.dosw.proyecto.model.ChangeRequest;
import eci.edu.dosw.proyecto.model.RequestStatus;
import eci.edu.dosw.proyecto.model.RequestType;
import eci.edu.dosw.proyecto.repository.AcademicPeriodRepository;
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
    private final AcademicPeriodRepository academicPeriodRepository;

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

    @Override
    public ReassignmentReportResponse generateCurrentPeriodReport() {
        AcademicPeriod currentPeriod = academicPeriodRepository.findByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("No active academic period found"));

        ReassignmentReportRequest request = new ReassignmentReportRequest();
        request.setStartDate(currentPeriod.getStartDate());
        request.setEndDate(currentPeriod.getEndDate());

        return generateReassignmentReport(request);
    }

    private List<ChangeRequest> filterRequests(ReassignmentReportRequest request) {
        return changeRequestRepository.findByCreationDateBetween(
                request.getStartDate(), request.getEndDate());
    }

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

    private String extractFacultyFromRequest(ChangeRequest request) {
        // Implementación placeholder - debes adaptar según tu modelo
        return "ENGINEERING";
    }

    private FacultyStatistics calculateFacultyStats(List<ChangeRequest> requests) {
        FacultyStatistics stats = new FacultyStatistics();
        stats.setTotalRequests(requests.size());

        long approved = requests.stream()
                .filter(req -> req.getStatus() == RequestStatus.APPROVED)
                .count();
        stats.setApproved((int) approved);
        stats.setApprovalRate(requests.size() > 0 ? (double) approved / requests.size() * 100 : 0.0);

        return stats;
    }

    private Map<String, SubjectStatistics> calculateSubjectStatistics(List<ChangeRequest> requests) {
        return requests.stream()
                .collect(Collectors.groupingBy(
                        req -> extractSubjectFromRequest(req),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                this::calculateSubjectStats
                        )
                ));
    }

    private String extractSubjectFromRequest(ChangeRequest request) {
        // Implementación placeholder
        return request.getTargetSubjectId() != null ? request.getTargetSubjectId() : "UNKNOWN_SUBJECT";
    }

    private SubjectStatistics calculateSubjectStats(List<ChangeRequest> requests) {
        SubjectStatistics stats = new SubjectStatistics();
        if (!requests.isEmpty()) {
            ChangeRequest first = requests.get(0);
            stats.setSubjectId(extractSubjectFromRequest(first));
            stats.setSubjectName("Subject-" + extractSubjectFromRequest(first)); // Placeholder
        }
        stats.setTotalRequests(requests.size());
        stats.setApproved((int) requests.stream().filter(req -> req.getStatus() == RequestStatus.APPROVED).count());
        return stats;
    }

    private Map<String, GroupStatistics> calculateGroupStatistics(List<ChangeRequest> requests) {
        return requests.stream()
                .collect(Collectors.groupingBy(
                        req -> extractGroupFromRequest(req),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                this::calculateGroupStats
                        )
                ));
    }

    private String extractGroupFromRequest(ChangeRequest request) {
        // Implementación placeholder
        return request.getTargetGroupId() != null ? request.getTargetGroupId() : "UNKNOWN_GROUP";
    }

    private GroupStatistics calculateGroupStats(List<ChangeRequest> requests) {
        GroupStatistics stats = new GroupStatistics();
        if (!requests.isEmpty()) {
            ChangeRequest first = requests.get(0);
            stats.setGroupId(extractGroupFromRequest(first));
            stats.setGroupCode("Group-" + extractGroupFromRequest(first)); // Placeholder
        }
        stats.setIncomingRequests(requests.size());
        stats.setOutgoingRequests(0); // Placeholder
        return stats;
    }

    private ReportPeriod createReportPeriod(ReassignmentReportRequest request) {
        ReportPeriod period = new ReportPeriod();
        period.setStartDate(request.getStartDate().toString());
        period.setEndDate(request.getEndDate().toString());
        period.setAcademicPeriod(determineAcademicPeriod(request.getStartDate()));
        return period;
    }

    private String determineAcademicPeriod(LocalDateTime date) {
        // Lógica para determinar el periodo académico
        return "2024-1";
    }
}