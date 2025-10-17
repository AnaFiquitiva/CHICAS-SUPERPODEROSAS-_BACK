package eci.edu.dosw.proyecto.dto;

import lombok.Data;

/**
 * DTO for reassignment statistics report response
 * Contains comprehensive statistics at global, faculty, subject, and group levels
 */
@Data
public class ReassignmentReportResponse {
    private GlobalStatistics global;
    private java.util.Map<String, FacultyStatistics> byFaculty;
    private java.util.Map<String, SubjectStatistics> bySubject;
    private java.util.Map<String, GroupStatistics> byGroup;
    private ReportPeriod period;
}
