package eci.edu.dosw.proyecto.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ReportResponse {
    private String id;
    private String reportType;
    private String title;
    private String description;
    private FacultyBasicResponse faculty;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Map<String, Object> reportData;
    private String format;
    private UserBasicResponse generatedBy;
    private LocalDateTime generatedAt;
    private String filePath;
}

