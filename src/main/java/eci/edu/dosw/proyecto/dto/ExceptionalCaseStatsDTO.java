package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class ExceptionalCaseStatsDTO {
    private Long totalCases;
    private Long pendingCases;
    private Long underReviewCases;
    private Long approvedCases;
    private Long rejectedCases;
    private Long needsInfoCases;
    private Long overdueCases;

    private Long academicCases;
    private Long medicalCases;
    private Long personalCases;
    private Long otherCases;

    private Double approvalRate;
    private Double averageResolutionTimeDays;
}