package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import java.util.Map;

@Data
public class RequestStatsDTO {
    // Estadísticas generales
    private Long totalRequests;
    private Long pendingRequests;
    private Long underReviewRequests;
    private Long approvedRequests;
    private Long rejectedRequests;
    private Long needsInfoRequests;

    // Estadísticas por tipo
    private Long groupChangeRequests;
    private Long subjectChangeRequests;
    private Long planChangeRequests;
    private Long newEnrollmentRequests;

    // Estadísticas por período
    private Long requestsThisWeek;
    private Long requestsThisMonth;
    private Long requestsThisYear;

    // Tendencias y métricas
    private Double approvalRate;
    private Double rejectionRate;
    private Double pendingRate;
    private Long averageProcessingTimeDays;

    // Distribución por programas
    private Map<String, Long> requestsByProgram;

    // Distribución por estado académico
    private Map<String, Long> requestsByAcademicStatus;

    // Métricas de tiempo
    private Long urgentRequests; // priority > 1
    private Long normalRequests; // priority = 1

    // Información del período actual
    private String currentActivePeriod;
    private Long activeStudentsWithRequests;
    private Long totalStudentsEligible;
}