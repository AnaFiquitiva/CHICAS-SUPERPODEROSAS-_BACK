package eci.edu.dosw.proyecto.utils;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.ChangeRequest;
import eci.edu.dosw.proyecto.model.PlanChangeDetail;
import eci.edu.dosw.proyecto.model.RequestHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    // Request to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestNumber", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "history", ignore = true)
    ChangeRequest toEntity(ChangeRequestRequestDTO requestDTO);

    PlanChangeDetail toPlanChangeDetailEntity(PlanChangeDetailRequestDTO dto);

    List<PlanChangeDetail> toPlanChangeDetailEntities(List<PlanChangeDetailRequestDTO> dtos);

    // Entity to Response
    ChangeRequestResponseDTO toResponseDTO(ChangeRequest changeRequest);

    PlanChangeDetailResponseDTO toPlanChangeDetailResponseDTO(PlanChangeDetail planChangeDetail);

    List<PlanChangeDetailResponseDTO> toPlanChangeDetailResponseDTOs(List<PlanChangeDetail> planChangeDetails);

    RequestHistoryResponseDTO toRequestHistoryResponseDTO(RequestHistory requestHistory);

    List<RequestHistoryResponseDTO> toRequestHistoryResponseDTOs(List<RequestHistory> requestHistories);

    List<ChangeRequestResponseDTO> toResponseDTOs(List<ChangeRequest> changeRequests);
}