// eci.edu.dosw.proyecto.service.interfaces.StudentService.java
package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
public interface StudentService {
    // CRUD Básico
    StudentResponse createStudent(StudentRequest studentRequest);
    StudentResponse getStudentById(String id);
    StudentResponse getStudentByCode(String code);
    StudentResponse getStudentByEmail(String email);
    StudentResponse updateStudent(String id, StudentRequest studentRequest);
    StudentResponse updatePersonalInfo(String id, StudentUpdateRequest updateRequest);
    void deleteStudent(String id);

    // Consultas
    List<StudentResponse> getAllStudents();
    List<StudentResponse> getStudentsByProgram(String programId);
    List<StudentResponse> getStudentsByFaculty(String facultyId);
    List<StudentResponse> getStudentsBySemester(Integer semester);
    List<StudentResponse> searchStudents(String searchTerm);
    StudentBasicResponse getStudentBasicInfo(String studentId);

    // Progreso Académico
    StudyPlanProgressResponse getStudyPlanProgress(String studentId);
    StudentScheduleResponse getStudentSchedule(String studentId, String academicPeriod);
    AcademicTrafficLightResponse calculateAcademicTrafficLight(String studentId);

    // Semáforo Académico
    void updateAcademicTrafficLight(String studentId);
    Double calculateStudentGPA(String studentId);
    void updateSubjectProgress(String studentId);
    boolean hasApprovedSubject(String studentId, String subjectId);
    boolean hasFailedSubject(String studentId, String subjectId);
    boolean canRetakeSubject(String studentId, String subjectId);

    // Validación
    boolean meetsAcademicRequirements(String studentId, String subjectId);

    // Estadísticas
    long countStudentsByProgram(String programId);
    long countStudentsByFaculty(String facultyId);
    long countByProgramAndActiveTrue(String programId);
    long countByProgramFacultyAndActiveTrue(String facultyId);

    StudentScheduleResponse getCurrentSemesterSchedule(String studentId);
    StudentScheduleResponse getHistoricalSchedule(String studentId, String academicPeriod);
    List<StudentScheduleResponse> getAllHistoricalSchedules(String studentId);

    AcademicTrafficLightResponse getAcademicTrafficLight(String studentId);
    AcademicTrafficLightResponse getStudentTrafficLight(String studentId);
    void recalculateAcademicTrafficLight(String studentId);
}