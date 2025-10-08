package eci.edu.dosw.proyecto.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
@Data
@Document(collection = "academic_periods")
/*
 * Entidad para periodos académicos habilitados para cambios
 */

public class AcademicPeriod {
    @Id
    private String id;
    private String periodName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private Boolean isActive;
    private Boolean allowGroupChanges;
    private Boolean allowSubjectChanges;
    private Integer maxRequestsPerStudent;
    private LocalDateTime createdDate;
    private String createdBy;

}