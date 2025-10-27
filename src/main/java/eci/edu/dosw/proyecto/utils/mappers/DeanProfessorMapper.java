// DeanProfessorMapper.java
package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.Dean;
import eci.edu.dosw.proyecto.model.Faculty;
import eci.edu.dosw.proyecto.model.Professor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {FacultyMapper.class})
public interface DeanProfessorMapper {

    // Dean Mappings
    @Mapping(source = "faculty", target = "faculty")
    DeanResponse toDeanResponse(Dean dean);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "personalEmail", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    Dean toDean(DeanRequest deanRequest);

    // Professor Mappings
    @Mapping(source = "faculty", target = "faculty")
    ProfessorResponse toProfessorResponse(Professor professor);

    ProfessorBasicResponse toProfessorBasicResponse(Professor professor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "personalEmail", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    Professor toProfessor(ProfessorRequest professorRequest);

    // Lists
    List<DeanResponse> toDeanResponseList(List<Dean> deans);
    List<ProfessorResponse> toProfessorResponseList(List<Professor> professors);
    List<ProfessorBasicResponse> toProfessorBasicResponseList(List<Professor> professors);
}