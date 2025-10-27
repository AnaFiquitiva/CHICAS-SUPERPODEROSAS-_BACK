package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StudyPlanProgressResponse {
    private String id;
    private StudentBasicResponse student;
    private ProgramResponse program;
    private Integer totalCredits;
    private Integer completedCredits;
    private Double progressPercentage;
    private List<SubjectProgressResponse> subjectProgress;
    private LocalDateTime calculatedAt;
}
