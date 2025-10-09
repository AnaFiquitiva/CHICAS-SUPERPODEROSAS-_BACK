package eci.edu.dosw.proyecto.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "exceptional_cases")
public class ExceptionalCase {
    @Id
    private String id;
    private String caseNumber;
    private String studentId;
    private String studentName;
    private String studentProgram;
    private Integer studentSemester;

    // Información del caso
    private String title;
    private String description;
    private String caseType; // ACADEMIC, PERSONAL, MEDICAL, OTHER
    private String justification;
    private String supportingDocuments; // URLs o nombres de documentos

    private RequestStatus status;
    private Integer priority; // 1=Baja, 2=Media, 3=Alta, 4=Urgente
    private String assignedTo; // Administrador/Decano asignado
    private LocalDateTime creationDate;
    private LocalDateTime reviewDate;
    private LocalDateTime resolutionDate;

    private String resolution;
    private String resolutionComments;
    private String resolvedBy;
    private LocalDateTime responseDeadline; // Fecha límite para respuesta

    private List<ExceptionalCaseHistory> history;
}