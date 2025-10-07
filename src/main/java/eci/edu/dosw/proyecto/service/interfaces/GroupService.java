package eci.edu.dosw.proyecto.service.interfaces;

/**
 * Servicio para operaciones relacionadas con grupos académicos
 */
public interface AcademicGroupService {
    String getFacultyBySubjectId(String subjectId);
    Boolean checkGroupCapacity(String groupId);
    Boolean checkScheduleConflict(String studentId, String newGroupId);
}