package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AcademicMapper {

    // Faculty Mappings
    FacultyResponse toFacultyResponse(Faculty faculty);
    FacultyBasicResponse toFacultyBasicResponse(Faculty faculty);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Faculty toFaculty(FacultyRequest facultyRequest);

    @Mapping(source = "faculty", target = "faculty")
    ProgramResponse toProgramResponse(Program program);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    Program toProgram(ProgramRequest programRequest);

    // Subject Mappings
    @Mapping(source = "faculty", target = "faculty")
    SubjectResponse toSubjectResponse(Subject subject);

    SubjectBasicResponse toSubjectBasicResponse(Subject subject);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    @Mapping(target = "prerequisites", ignore = true)
    Subject toSubject(SubjectRequest subjectRequest);

    // Lists
    List<FacultyResponse> toFacultyResponseList(List<Faculty> faculties);
    List<ProgramResponse> toProgramResponseList(List<Program> programs);
    List<SubjectResponse> toSubjectResponseList(List<Subject> subjects);
    List<SubjectBasicResponse> toSubjectBasicResponseList(List<Subject> subjects);
}