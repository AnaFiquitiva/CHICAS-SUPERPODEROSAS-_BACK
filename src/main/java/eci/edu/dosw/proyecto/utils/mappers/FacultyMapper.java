package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.FacultyBasicResponse;
import eci.edu.dosw.proyecto.dto.FacultyResponse;
import eci.edu.dosw.proyecto.model.Faculty;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FacultyMapper {

    FacultyResponse toFacultyResponse(Faculty faculty);
    FacultyBasicResponse toFacultyBasicResponse(Faculty faculty);
}