package eci.edu.dosw.proyecto.service.interfaces;


import eci.edu.dosw.proyecto.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
public interface ReportService {
    ReportResponse generateReport(ReportRequest reportRequest);
    ReportResponse getReportById(String id);
    List<ReportResponse> getReportsByUser(String userId);
    List<ReportResponse> getReportsByFaculty(String facultyId);

    // Specific reports
    ApprovalStatsResponse getApprovalStatistics(ReportRequest reportRequest);
    List<GroupDemandResponse> getMostRequestedGroups(ReportRequest reportRequest);
    ReassignmentStatsResponse getReassignmentStatistics(ReportRequest reportRequest);
    OccupancyReportResponse getGroupOccupancyReport(ReportRequest reportRequest);
    List<SpecialApprovalCaseResponse> getSpecialApprovalCases(ReportRequest reportRequest);

    // Real-time statistics
    RealTimeStatsResponse getRealTimeStatistics(String statType, String facultyId);

    // Export capabilities
    String exportReportToPdf(String reportId);
    String exportReportToExcel(String reportId);
}