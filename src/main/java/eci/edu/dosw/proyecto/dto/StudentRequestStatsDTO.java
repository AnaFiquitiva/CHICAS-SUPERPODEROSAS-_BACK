package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class StudentRequestStatsDTO {
    private Long totalRequests;
    private Long pendingRequests;
    private Long underReviewRequests;
    private Long approvedRequests;
    private Long rejectedRequests;
    private Long needsInfoRequests;

    // Límites y disponibilidad
    private Integer maxRequestsAllowed;
    private Integer remainingRequests;
    private Boolean canCreateNewRequest;
    private String availabilityMessage;

    // Tipos de solicitud
    private Long groupChangeRequests;
    private Long subjectChangeRequests;
    private Long planChangeRequests;
    private Long newEnrollmentRequests;

    // Tendencias y tiempos
    private Double approvalRate;
    private Double rejectionRate;
    private Long averageProcessingDays;

    // Información del período actual
    private String currentAcademicPeriod;
    private Boolean isPeriodActive;
}
