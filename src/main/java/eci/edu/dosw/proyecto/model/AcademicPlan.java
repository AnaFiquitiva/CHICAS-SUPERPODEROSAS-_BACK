package eci.edu.dosw.proyecto.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Data
@Document(collection = "academic_plans")
/*
 * Plan de estudios
 */
public class AcademicPlan {
    @Id
    private String id;
    private String name;
    private StudentType studentType;
    private String program;
    private List<Subject> availableSubjects;
    private Boolean active;

}
