package eci.edu.dosw.proyecto.utils;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.ExceptionalCase;
import eci.edu.dosw.proyecto.model.ExceptionalCaseHistory;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExceptionalCaseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "caseNumber", ignore = true)
    @Mapping(target = "studentName", ignore = true)
    @Mapping(target = "studentProgram", ignore = true)
    @Mapping(target = "studentSemester", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "reviewDate", ignore = true)
    @Mapping(target = "resolutionDate", ignore = true)
    @Mapping(target = "resolution", ignore = true)
    @Mapping(target = "resolutionComments", ignore = true)
    @Mapping(target = "resolvedBy", ignore = true)
    @Mapping(target = "responseDeadline", ignore = true)
    @Mapping(target = "history", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    ExceptionalCase toEntity(ExceptionalCaseRequestDTO dto);

    ExceptionalCaseResponseDTO toDTO(ExceptionalCase entity);

    List<ExceptionalCaseResponseDTO> toDTOList(List<ExceptionalCase> entities);

    ExceptionalCaseHistoryDTO historyToDTO(ExceptionalCaseHistory history);

    List<ExceptionalCaseHistoryDTO> historyListToDTO(List<ExceptionalCaseHistory> history);
}