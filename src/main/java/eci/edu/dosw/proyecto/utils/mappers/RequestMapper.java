package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    // === MAPEOS PRINCIPALES ===
    @Mapping(source = "student", target = "student")
    @Mapping(source = "currentGroup", target = "currentGroup")
    @Mapping(source = "requestedGroup", target = "requestedGroup")
    @Mapping(source = "requestedSubject", target = "requestedSubject")
    @Mapping(source = "processedBy", target = "processedBy")
    RequestResponse toRequestResponse(Request request);

    // === Request básico (DTO resumen) ===
    @Mapping(target = "studentName",
            expression = "java(request.getStudent() != null ? request.getStudent().getFirstName() + \" \" + request.getStudent().getLastName() : null)")
    @Mapping(source = "student.code", target = "studentCode")
    @Mapping(source = "student.program.name", target = "programName")
    RequestBasicResponse toRequestBasicResponse(Request request);

    // === Crear Request desde DTO ===
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "currentGroup", ignore = true)
    @Mapping(target = "requestedGroup", ignore = true)
    @Mapping(target = "currentSubject", ignore = true)
    @Mapping(target = "requestedSubject", ignore = true)
    @Mapping(target = "processedBy", ignore = true)
    @Mapping(target = "processedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "specialApproval", ignore = true)
    @Mapping(target = "specialApprovalJustification", ignore = true)
    @Mapping(target = "priorityScore", ignore = true) // ✅ CORREGIDO
    Request toRequest(RequestCreateRequest requestCreateRequest);

    // === Listas ===
    List<RequestResponse> toRequestResponseList(List<Request> requests);
    List<RequestBasicResponse> toRequestBasicResponseList(List<Request> requests);

    // === Actualización desde DTO ===
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestNumber", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "currentGroup", ignore = true)
    @Mapping(target = "requestedGroup", ignore = true)
    @Mapping(target = "currentSubject", ignore = true)
    @Mapping(target = "requestedSubject", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "processedBy", ignore = true)
    @Mapping(target = "processedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "specialApproval", ignore = true)
    @Mapping(target = "specialApprovalJustification", ignore = true)
    @Mapping(target = "priorityScore", ignore = true) // ✅ CORREGIDO
    void updateRequestFromRequest(RequestUpdateRequest updateRequest, @MappingTarget Request request);

    // === Historial ===
    RequestHistoryResponse toRequestHistoryResponse(RequestHistory requestHistory);
    List<RequestHistoryResponse> toRequestHistoryResponseList(List<RequestHistory> requestHistories);

    // === Actualización parcial ===
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRequestFromEntity(Request source, @MappingTarget Request target);

    // === Conversión de Role ↔ String ===
    default String map(Role role) {
        return role != null ? role.getName() : null;
    }

    default Role map(String roleName) {
        if (roleName == null) return null;
        Role role = new Role();
        role.setName(roleName);
        return role;
    }
}
