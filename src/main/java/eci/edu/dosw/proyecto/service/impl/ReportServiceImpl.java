package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.ForbiddenException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.ReportService;
import eci.edu.dosw.proyecto.service.interfaces.WaitingListService;
import eci.edu.dosw.proyecto.utils.mappers.ReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static eci.edu.dosw.proyecto.service.impl.GroupServiceImpl.getUser;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final RequestRepository requestRepository;
    private final SpecialApprovalCaseRepository specialApprovalCaseRepository;
    private final FacultyRepository facultyRepository;
    private final UserRepository userRepository;
    private final DeanRepository deanRepository;
    private final GroupRepository groupRepository;
    private final AlertRepository alertRepository;
    private final WaitingListService waitingListService;
    private final ReportMapper reportMapper;

    @Override
    @Transactional
    public ReportResponse generateReport(ReportRequest reportRequest) {
        User currentUser = getCurrentAuthenticatedUser();
        validateReportAccess(reportRequest.getReportType(), currentUser, reportRequest.getFacultyId());

        Report report = reportMapper.toReport(reportRequest);
        report.setGeneratedBy(currentUser);
        report.setGeneratedAt(LocalDateTime.now());
        report.setTitle(generateReportTitle(reportRequest.getReportType()));
        report.setDescription(generateReportDescription(reportRequest.getReportType()));

        // Generar datos según el tipo de reporte
        Map<String, Object> reportData = switch (reportRequest.getReportType()) {
            case "MOST_REQUESTED_GROUPS" -> generateMostRequestedGroupsReport(reportRequest);
            case "APPROVAL_VS_REJECTION_RATES" -> generateApprovalRejectionReport(reportRequest);
            case "REASSIGNMENT_STATISTICS" -> generateReassignmentStatisticsReport(reportRequest);
            case "EXCEPTIONAL_CASES" -> generateExceptionalCasesReport(reportRequest);
            default ->
                    throw new IllegalArgumentException("Tipo de reporte no soportado: " + reportRequest.getReportType());
        };

        report.setReportData(reportData);
        report.calculateRates();

        Report savedReport = reportRepository.save(report);
        return reportMapper.toReportResponse(savedReport);
    }

    private Map<String, Object> generateApprovalRejectionReport(ReportRequest reportRequest) {
        ApprovalStatsResponse stats = getApprovalStatistics(reportRequest);
        Map<String, Object> data = new HashMap<>();
        data.put("approvalStats", stats);
        return data;
    }

    private Map<String, Object> generateReassignmentStatisticsReport(ReportRequest reportRequest) {
        ReassignmentStatsResponse stats = getReassignmentStatistics(reportRequest);
        Map<String, Object> data = new HashMap<>();
        data.put("reassignmentStats", stats);
        return data;
    }

    private Map<String, Object> generateExceptionalCasesReport(ReportRequest reportRequest) {
        List<SpecialApprovalCaseResponse> cases = getSpecialApprovalCases(reportRequest);
        Map<String, Object> data = new HashMap<>();
        data.put("specialApprovalCases", cases);
        return data;
    }

    private Map<String, Object> generateMostRequestedGroupsReport(ReportRequest reportRequest) {
        List<GroupDemandResponse> groupDemands = getMostRequestedGroups(reportRequest);
        Map<String, Object> data = new HashMap<>();
        data.put("groupDemands", groupDemands);
        return data;
    }

    @Override
    public ReportResponse getReportById(String id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reporte", id));
        validateReportAccess(report.getReportType(), getCurrentAuthenticatedUser(),
                report.getFaculty() != null ? report.getFaculty().getId() : null);
        return reportMapper.toReportResponse(report);
    }

    @Override
    public List<ReportResponse> getReportsByUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario", userId));
        List<Report> reports = reportRepository.findByGeneratedBy(user);
        return reports.stream()
                .map(reportMapper::toReportResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportResponse> getReportsByFaculty(String facultyId) {
        User currentUser = getCurrentAuthenticatedUser();
        validateFacultyAccess(facultyId, currentUser);
        List<Report> reports = reportRepository.findByFacultyId(facultyId);
        return reports.stream()
                .map(reportMapper::toReportResponse)
                .collect(Collectors.toList());
    }

    // === MÉTODOS ESPECÍFICOS DE LA INTERFAZ ===

    @Override
    public ApprovalStatsResponse getApprovalStatistics(ReportRequest reportRequest) {
        String facultyId = reportRequest.getFacultyId();
        LocalDateTime startDate = reportRequest.getStartDate();
        LocalDateTime endDate = reportRequest.getEndDate();

        long total = requestRepository.countByStudentProgramFacultyAndCreatedAtBetween(facultyId, startDate, endDate);
        long approved = requestRepository.countByStudentProgramFacultyAndStatusAndCreatedAtBetween(
                facultyId, RequestStatus.APPROVED, startDate, endDate);
        long rejected = requestRepository.countByStudentProgramFacultyAndStatusAndCreatedAtBetween(
                facultyId, RequestStatus.REJECTED, startDate, endDate);

        ApprovalStatsResponse stats = new ApprovalStatsResponse();
        stats.setFacultyId(facultyId);
        stats.setFacultyName(getFacultyName(facultyId));
        stats.setTotalRequests((int) total);
        stats.setApprovedRequests((int) approved);
        stats.setRejectedRequests((int) rejected);
        stats.setPendingRequests((int) requestRepository.countByStudentProgramFacultyAndStatusAndCreatedAtBetween(
                facultyId, RequestStatus.PENDING, startDate, endDate));



        if (total > 0) {
            stats.setApprovalRate(Math.round((approved * 100.0 / total) * 100.0) / 100.0);
            stats.setRejectionRate(Math.round((rejected * 100.0 / total) * 100.0) / 100.0);
        } else {
            stats.setApprovalRate(0.0);
            stats.setRejectionRate(0.0);
        }

        stats.setCalculatedAt(LocalDateTime.now());
        return stats;
    }

    @Override
    public List<GroupDemandResponse> getMostRequestedGroups(ReportRequest reportRequest) {
        String facultyId = reportRequest.getFacultyId();
        LocalDateTime startDate = reportRequest.getStartDate();
        LocalDateTime endDate = reportRequest.getEndDate();

        List<Group> groups = groupRepository.findByFacultyIdAndActiveTrue(facultyId);
        List<GroupDemandResponse> demands = groups.stream()
                .map(group -> {
                    GroupDemandResponse d = new GroupDemandResponse();
                    d.setGroupId(group.getId());
                    d.setGroupCode(group.getGroupCode());
                    d.setSubjectName(group.getSubject().getName());
                    d.setTotalRequests((int) requestRepository.countByRequestedGroupAndCreatedAtBetween(group.getId(), startDate, endDate));
                    d.setApprovedRequests((int) requestRepository.countByRequestedGroupAndStatusAndCreatedAtBetween(group.getId(), RequestStatus.APPROVED, startDate, endDate));
                    d.setOccupancyPercentage(group.getOccupancyPercentage()); // ✅ Ahora es Double
                    d.setWaitingListCount((int) waitingListService.getWaitingListSize(group.getId())); // ✅ Asegúrate de que este método existe
                    return d;
                })
                .sorted((a, b) -> Integer.compare(b.getTotalRequests(), a.getTotalRequests()))
                .collect(Collectors.toList());

        // Asignar ranking
        for (int i = 0; i < demands.size(); i++) {
            demands.get(i).setDemandRank(i + 1);
        }

        return demands;
    }

    @Override
    public ReassignmentStatsResponse getReassignmentStatistics(ReportRequest reportRequest) {
        String facultyId = reportRequest.getFacultyId();
        LocalDateTime startDate = reportRequest.getStartDate();
        LocalDateTime endDate = reportRequest.getEndDate();

        List<Request> requests = requestRepository.findByFacultyIdAndCreatedAtBetween(facultyId, startDate, endDate);

        Map<String, Integer> byType = new HashMap<>();
        Map<String, Integer> bySubject = new HashMap<>();
        Map<String, Integer> byFaculty = new HashMap<>();

        int successful = 0;
        int failed = 0;

        for (Request requestItem : requests) {
            // Contar por tipo
            String type = requestItem.getType().name();
            byType.put(type, byType.getOrDefault(type, 0) + 1);

            // Contar por materia
            if (requestItem.getRequestedSubject() != null) {
                String subjectName = requestItem.getRequestedSubject().getName();
                bySubject.put(subjectName, bySubject.getOrDefault(subjectName, 0) + 1);
            }

            // Contar éxitos y fracasos
            if (RequestStatus.APPROVED.equals(requestItem.getStatus()) ||
                    RequestStatus.SPECIAL_APPROVAL.equals(requestItem.getStatus())) {
                successful++;
            } else if (RequestStatus.REJECTED.equals(requestItem.getStatus())) {
                failed++;
            }

            // Contar por facultad (aunque sea la misma)
            String facultyName = requestItem.getStudent().getProgram().getFaculty().getName();
            byFaculty.put(facultyName, byFaculty.getOrDefault(facultyName, 0) + 1);
        }

        ReassignmentStatsResponse stats = new ReassignmentStatsResponse();
        stats.setPeriod(generatePeriodString(startDate, endDate));
        stats.setFacultyId(facultyId);
        stats.setFacultyName(getFacultyName(facultyId));
        stats.setTotalReassignments(requests.size());
        stats.setSuccessfulReassignments(successful);
        stats.setFailedReassignments(failed);
        stats.setSuccessRate(!requests.isEmpty() ? (successful * 100.0 / requests.size()) : 0.0);
        stats.setReassignmentsByFaculty(byFaculty);
        stats.setReassignmentsBySubject(bySubject);
        stats.setReassignmentsByType(byType);
        stats.setGeneratedAt(LocalDateTime.now());
        return stats;
    }

    @Override
    public OccupancyReportResponse getGroupOccupancyReport(ReportRequest reportRequest) {
        String facultyId = reportRequest.getFacultyId();
        LocalDateTime startDate = reportRequest.getStartDate();
        LocalDateTime endDate = reportRequest.getEndDate();

        List<Group> groups = groupRepository.findByFacultyIdAndActiveTrue(facultyId);
        List<GroupCapacityResponse> capacities = groups.stream()
                .map(group -> {
                    GroupCapacityResponse response = new GroupCapacityResponse();
                    response.setGroupId(group.getId());
                    response.setGroupCode(group.getGroupCode());
                    response.setSubjectName(group.getSubject().getName());
                    response.setMaxCapacity(group.getMaxCapacity());
                    response.setCurrentEnrollment(group.getCurrentEnrollment());
                    response.setAvailableSpots(group.getMaxCapacity() - group.getCurrentEnrollment());
                    response.setOccupancyPercentage(group.getOccupancyPercentage());
                    response.setHasAvailableSpots(group.hasAvailableSpots());
                    response.setWaitingListCount((int) waitingListService.getWaitingListSize(group.getId()));
                    return response;
                })
                .collect(Collectors.toList());

        OccupancyReportResponse report = new OccupancyReportResponse();
        report.setFacultyId(facultyId);
        report.setFacultyName(getFacultyName(facultyId));
        report.setTotalGroups(groups.size());
        report.setFullGroups((int) groups.stream().filter(group -> !group.hasAvailableSpots()).count());
        report.setHighOccupancyGroups((int) groups.stream().filter(group -> group.getOccupancyPercentage() >= 90.0).count());
        report.setAvailableGroups((int) groups.stream().filter(Group::hasAvailableSpots).count());
        report.setAverageOccupancy(groups.stream().mapToDouble(Group::getOccupancyPercentage).average().orElse(0.0));
        report.setGroupDetails(capacities);
        report.setGeneratedAt(LocalDateTime.now());
        return report;
    }

    @Override
    public List<SpecialApprovalCaseResponse> getSpecialApprovalCases(ReportRequest reportRequest) {
        LocalDateTime startDate = reportRequest.getStartDate();
        LocalDateTime endDate = reportRequest.getEndDate();

        List<SpecialApprovalCase> cases = specialApprovalCaseRepository.findByApprovedAtBetween(startDate, endDate);

        return cases.stream()
                .map(caseItem -> {
                    SpecialApprovalCaseResponse response = new SpecialApprovalCaseResponse();
                    response.setId(caseItem.getId());
                    response.setRequestNumber(caseItem.getRequest().getRequestNumber());
                    response.setStudentName(caseItem.getRequest().getStudent().getFirstName() + " " +
                            caseItem.getRequest().getStudent().getLastName());
                    response.setStudentCode(caseItem.getRequest().getStudent().getCode());
                    response.setProgramName(caseItem.getRequest().getStudent().getProgram().getName());
                    response.setSubjectName(caseItem.getRequest().getRequestedSubject().getName());
                    response.setGroupCode(caseItem.getRequest().getRequestedGroup().getGroupCode());
                    response.setApprovedByName(caseItem.getApprovedBy().getUsername());
                    response.setJustification(caseItem.getJustification());
                    response.setConstraintsOverridden(caseItem.getConstraintsOverridden());
                    response.setApprovedAt(caseItem.getApprovedAt());
                    response.setRequestType(caseItem.getRequest().getType());
                    response.setFacultyName(caseItem.getRequest().getStudent().getProgram().getFaculty().getName());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public RealTimeStatsResponse getRealTimeStatistics(String statType, String facultyId) {
        User currentUser = getCurrentAuthenticatedUser();
        validateFacultyAccess(facultyId, currentUser);

        RealTimeStatsResponse stats = new RealTimeStatsResponse();
        stats.setStatType(statType);
        stats.setFacultyId(facultyId);
        stats.setFacultyName(getFacultyName(facultyId));
        stats.setCalculatedAt(LocalDateTime.now());

        switch (statType) {
            case "REQUESTS_BY_FACULTY":
                long totalRequests = requestRepository.countByStudentProgramFaculty(facultyId);
                long pending = requestRepository.countByStudentProgramFacultyAndStatus(facultyId, RequestStatus.PENDING);
                long approved = requestRepository.countByStudentProgramFacultyAndStatus(facultyId, RequestStatus.APPROVED);
                long rejected = requestRepository.countByStudentProgramFacultyAndStatus(facultyId, RequestStatus.REJECTED);
                stats.setStatistics(Map.of(
                        "totalRequests", totalRequests,
                        "pending", pending,
                        "approved", approved,
                        "rejected", rejected
                ));
                break;
            case "GROUP_OCCUPANCY":
                List<Group> groups = groupRepository.findByFacultyIdAndActiveTrue(facultyId);
                double avgOccupancy = groups.stream().mapToDouble(Group::getOccupancyPercentage).average().orElse(0.0);
                int fullGroups = (int) groups.stream().filter(group -> !group.hasAvailableSpots()).count();
                int highOccupancy = (int) groups.stream().filter(group -> group.getOccupancyPercentage() >= 90.0).count();
                int available = (int) groups.stream().filter(Group::hasAvailableSpots).count();
                stats.setStatistics(Map.of(
                        "totalGroups", groups.size(),
                        "fullGroups", fullGroups,
                        "highOccupancyGroups", highOccupancy,
                        "availableGroups", available,
                        "averageOccupancy", avgOccupancy
                ));
                break;
            case "ALERTS":
                List<Alert> alerts = alertRepository.findByFacultyIdAndResolvedFalse(facultyId);
                stats.setStatistics(Map.of(
                        "totalAlerts", alerts.size(),
                        "groupCapacityAlerts", (int) alerts.stream().filter(alert -> alert.getType() == AlertType.GROUP_CAPACITY_90).count(),
                        "academicProgressAlerts", (int) alerts.stream().filter(alert -> alert.getType() == AlertType.ACADEMIC_PROGRESS).count()
                ));
                break;
            default:
                throw new IllegalArgumentException("Tipo de estadística no soportado: " + statType);
        }

        return stats;
    }

    @Override
    public String exportReportToPdf(String reportId) {
        // Implementar lógica para generar PDF
        return "/reports/" + reportId + ".pdf";
    }

    @Override
    public String exportReportToExcel(String reportId) {
        // Implementar lógica para generar Excel
        return "/reports/" + reportId + ".xlsx";
    }

    // === MÉTODOS AUXILIARES ===

    private String generateReportTitle(String reportType) {
        return switch (reportType) {
            case "MOST_REQUESTED_GROUPS" -> "Grupos más solicitados";
            case "APPROVAL_VS_REJECTION_RATES" -> "Tasa de aprobación vs rechazos";
            case "REASSIGNMENT_STATISTICS" -> "Estadísticas de reasignación";
            case "EXCEPTIONAL_CASES" -> "Casos excepcionales";
            default -> "Reporte";
        };
    }

    private String generateReportDescription(String reportType) {
        return switch (reportType) {
            case "MOST_REQUESTED_GROUPS" -> "Ranking de grupos con mayor número de solicitudes";
            case "APPROVAL_VS_REJECTION_RATES" -> "Análisis comparativo de solicitudes aprobadas y rechazadas";
            case "REASSIGNMENT_STATISTICS" -> "Estadísticas detalladas de solicitudes de reasignación";
            case "EXCEPTIONAL_CASES" -> "Listado de solicitudes aprobadas mediante mecanismo especial";
            default -> "Reporte generado automáticamente";
        };
    }

    private String generatePeriodString(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return "Todo el período";
        }
        return startDate.toLocalDate() + " - " + endDate.toLocalDate();
    }

    private String getFacultyName(String facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new NotFoundException("Facultad", facultyId));
        return faculty.getName();
    }

    private void validateReportAccess(String reportType, User user, String facultyId) {
        String role = user.getRole().getName();

        if ("ADMIN".equals(role)) return;

        if ("DEAN".equals(role)) {
            if ("EXCEPTIONAL_CASES".equals(reportType)) {
                throw new ForbiddenException("Solo los administradores pueden ver reportes de casos excepcionales");
            }
            if (facultyId != null) {
                validateFacultyAccess(facultyId, user);
            }
            return;
        }

        throw new ForbiddenException("No tienes permiso para generar reportes");
    }

    private void validateFacultyAccess(String facultyId, User user) {
        String role = user.getRole().getName();
        if ("ADMIN".equals(role)) return;
        if ("DEAN".equals(role)) {
            Dean dean = deanRepository.findByUser(user)
                    .orElseThrow(() -> new NotFoundException("Decano", user.getId()));
            if (!dean.getFaculty().getId().equals(facultyId)) {
                throw new ForbiddenException("No puedes acceder a reportes de otra facultad");
            }
            return;
        }
        throw new ForbiddenException("No tienes permiso para acceder a reportes de esta facultad");
    }

    private User getCurrentAuthenticatedUser() {
        return getUser(userRepository);
    }
}