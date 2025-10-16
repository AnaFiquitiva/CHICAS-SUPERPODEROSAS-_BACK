package eci.edu.dosw.proyecto.service.interfaces;

public interface PermissionService {
    boolean canViewAllRequests(String employeeCode);
    boolean canApprovePlanChanges(String employeeCode);
    boolean canManageAcademicPeriods(String employeeCode);
    boolean canGenerateReports(String employeeCode);
    boolean canManageStudents(String employeeCode);
    boolean isDeanOfFaculty(String employeeCode, String facultyId);
}