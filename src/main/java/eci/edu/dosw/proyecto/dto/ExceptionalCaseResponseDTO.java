package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExceptionalCaseResponseDTO {
    private String id;
    private String caseNumber;
    private String studentId;
    private String studentName;
    private String studentProgram;
    private Integer studentSemester;
    private String title;
    private String description;
    private String caseType;
    private String justification;
    private String supportingDocuments;
    private String status;
    private Integer priority;
    private String assignedTo;
    private LocalDateTime creationDate;
    private LocalDateTime reviewDate;
    private LocalDateTime resolutionDate;
    private String resolution;
    private String resolutionComments;
    private String resolvedBy;
    private LocalDateTime responseDeadline;
    private List<ExceptionalCaseHistoryDTO> history;
}