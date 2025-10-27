package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportRequest {
    @NotBlank(message = "Tipo de reporte es requerido")
    private String reportType;

    private String facultyId;
    private String academicPeriod;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String format; // PDF, EXCEL, JSON
}
