/**
 * DTO for reassignment statistics report request
 * Contains filters for generating customized reports
 */
package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ReassignmentReportRequest {
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    private String faculty; // Optional filter
    private String subjectId; // Optional filter
    private String groupId; // Optional filter
}

