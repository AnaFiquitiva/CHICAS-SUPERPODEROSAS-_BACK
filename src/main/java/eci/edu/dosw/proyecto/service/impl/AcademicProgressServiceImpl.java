/**
 * Implementation of academic progress tracking service
 * Handles business logic for progress calculation and status determination
 */
package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.AcademicProgressResponse;
import eci.edu.dosw.proyecto.dto.SubjectProgress;
import eci.edu.dosw.proyecto.exception.StudentNotFoundException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.AcademicProgressRepository;
import eci.edu.dosw.proyecto.service.interfaces.AcademicProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcademicProgressServiceImpl implements AcademicProgressService {

    private final AcademicProgressRepository academicProgressRepository;

    @Override
    public AcademicProgressResponse getAcademicProgress(String studentCode) {
        log.info("Retrieving academic progress for student code: {}", studentCode);

        Student student = academicProgressRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with code: " + studentCode));

        return calculateAcademicProgress(student);
    }

    @Override
    public AcademicProgressResponse getAcademicProgressByStudentId(String studentId) {
        log.info("Retrieving academic progress for student ID: {}", studentId);

        Student student = academicProgressRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentId));

        return calculateAcademicProgress(student);
    }

    /**
     * Calculates comprehensive academic progress for a student
     * @param student Student entity
     * @return Academic progress response with detailed information
     */
    private AcademicProgressResponse calculateAcademicProgress(Student student) {
        AcademicProgressResponse response = new AcademicProgressResponse();
        response.setStudentId(student.getId());
        response.setStudentName(student.getName());
        response.setProgram(student.getProgram());
        response.setCurrentSemester(student.getCurrentSemester());

        // Calculate completed and pending subjects
        List<SubjectProgress> completedSubjects = calculateCompletedSubjects(student);
        List<SubjectProgress> pendingSubjects = calculatePendingSubjects(student, completedSubjects);

        response.setCompletedSubjects(completedSubjects);
        response.setPendingSubjects(pendingSubjects);

        // Calculate credit metrics
        int totalCredits = calculateTotalCredits(completedSubjects, pendingSubjects);
        int completedCredits = calculateCompletedCredits(completedSubjects);

        response.setTotalCredits(totalCredits);
        response.setCompletedCredits(completedCredits);
        response.setPendingCredits(totalCredits - completedCredits);
        response.setProgressPercentage(calculateProgressPercentage(totalCredits, completedCredits));

        response.setStatus(determineAcademicStatus(student, response.getProgressPercentage()));

        log.debug("Academic progress calculated for student {}: {}% complete",
                student.getName(), response.getProgressPercentage());

        return response;
    }

    /**
     * Calculates completed subjects based on student's enrollment history
     * @param student Student entity
     * @return List of completed subjects with grades
     */
    private List<SubjectProgress> calculateCompletedSubjects(Student student) {
        // Implementation logic to determine completed subjects
        // This would typically query enrollment records with COMPLETED status
        List<SubjectProgress> completedSubjects = new ArrayList<>();

        // Example implementation - replace with actual logic
        // completedSubjects = enrollmentService.getCompletedSubjects(student.getId());

        return completedSubjects;
    }

    /**
     * Calculates pending subjects based on academic plan and completed subjects
     * @param student Student entity
     * @param completedSubjects List of already completed subjects
     * @return List of pending subjects
     */
    private List<SubjectProgress> calculatePendingSubjects(Student student,
                                                           List<SubjectProgress> completedSubjects) {
        // Implementation logic to determine pending subjects
        List<SubjectProgress> pendingSubjects = new ArrayList<>();

        // Example implementation - replace with actual logic
        // pendingSubjects = academicPlanService.getPendingSubjects(student.getAcademicPlanId(), completedSubjects);

        return pendingSubjects;
    }

    private int calculateTotalCredits(List<SubjectProgress> completed, List<SubjectProgress> pending) {
        return completed.stream().mapToInt(SubjectProgress::getCredits).sum() +
                pending.stream().mapToInt(SubjectProgress::getCredits).sum();
    }

    private int calculateCompletedCredits(List<SubjectProgress> completedSubjects) {
        return completedSubjects.stream().mapToInt(SubjectProgress::getCredits).sum();
    }

    private Double calculateProgressPercentage(int totalCredits, int completedCredits) {
        return totalCredits > 0 ? (double) completedCredits / totalCredits * 100 : 0.0;
    }

    /**
     * Determines academic status based on progress and semester
     * @param student Student entity
     * @param progressPercentage Current progress percentage
     * @return Academic status string
     */
    private String determineAcademicStatus(Student student, Double progressPercentage) {
        int currentSemester = student.getCurrentSemester();
        double expectedProgress = (double) currentSemester / 10 * 100; // Assuming 10 semesters total

        if (progressPercentage >= expectedProgress + 10) return "AHEAD";
        if (progressPercentage >= expectedProgress - 10) return "IN_PROGRESS";
        return "DELAYED";
    }
}

