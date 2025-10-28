package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.ProgramResponse;
import eci.edu.dosw.proyecto.model.Program;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProgramMapper {

    @Mapping(source = "faculty", target = "faculty")
    ProgramResponse toProgramResponse(Program program);
}