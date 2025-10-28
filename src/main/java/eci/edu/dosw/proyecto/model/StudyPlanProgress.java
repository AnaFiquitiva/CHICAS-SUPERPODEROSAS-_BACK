package eci.edu.dosw.proyecto.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase que representa el avance en el plan de estudios de un estudiante.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "study_plan_progress")
public class StudyPlanProgress {
    @Id
    private String id;

    @DBRef
    private Student student;

    @DBRef
    private Program program;

    private Integer totalCredits;
    private Integer completedCredits;
    private Double progressPercentage;

    @DBRef
    private List<SubjectProgress> subjectProgress;

    private LocalDateTime calculatedAt;
}