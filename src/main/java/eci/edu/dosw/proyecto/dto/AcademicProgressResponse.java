/**
 * DTO for academic progress tracking response
 * Contains student's progress information in their study plan
 */
package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import java.util.List;

@Data
public class AcademicProgressResponse {
    private String studentId;
    private String studentName;
    private String program;
    private Integer currentSemester;
    private Double progressPercentage;
    private Integer totalCredits;
    private Integer completedCredits;
    private Integer pendingCredits;
    private List<SubjectProgress> completedSubjects;
    private List<SubjectProgress> pendingSubjects;
    private String status; // "IN_PROGRESS", "COMPLETED", "DELAYED"
}

