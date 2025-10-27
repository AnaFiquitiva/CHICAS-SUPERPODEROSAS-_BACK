package eci.edu.dosw.proyecto.service.interfaces;


import eci.edu.dosw.proyecto.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
public interface AcademicService {
    // Faculty operations
    FacultyResponse createFaculty(FacultyRequest facultyRequest);
    FacultyResponse getFacultyById(String id);
    FacultyResponse getFacultyByCode(String code);
    List<FacultyResponse> getAllFaculties();
    FacultyResponse updateFaculty(String facultyId, FacultyRequest facultyRequest);
    void deactivateFaculty(String facultyId);

    // Program operations
    ProgramResponse createProgram(ProgramRequest programRequest);
    ProgramResponse getProgramById(String id);
    ProgramResponse getProgramByCode(String code);
    List<ProgramResponse> getProgramsByFaculty(String facultyId);
    List<ProgramResponse> getAllPrograms();
    ProgramResponse updateProgram(String programId, ProgramRequest programRequest);
    void deactivateProgram(String programId);

    // Subject operations
    SubjectResponse createSubject(SubjectRequest subjectRequest);
    SubjectResponse getSubjectById(String id);
    SubjectResponse getSubjectByCode(String code);
    List<SubjectResponse> getSubjectsByFaculty(String facultyId);
    List<SubjectResponse> getAllSubjects();
    List<SubjectResponse> searchSubjects(String searchTerm);
    SubjectResponse updateSubject(String subjectId, SubjectRequest subjectRequest);
    void deactivateSubject(String subjectId);
    boolean validatePrerequisites(String studentId, String subjectId);

    // Academic Period operations
    AcademicPeriodResponse getCurrentAcademicPeriod();
    List<AcademicPeriodResponse> getActiveAcademicPeriods();
    boolean isRequestPeriodActive(String facultyId);
    String getCurrentAcademicPeriodName();

}