package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {StudentMapper.class, AcademicMapper.class, GroupMapper.class, UserMapper.class})
public interface ManualAssignmentMapper {

    // ManualAssignment Mappings
    @Mapping(source = "student", target = "student")
    @Mapping(source = "subject", target = "subject")
    @Mapping(source = "group", target = "group")
    @Mapping(source = "assignedBy", target = "assignedBy")
    ManualAssignmentResponse toManualAssignmentResponse(ManualAssignment manualAssignment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "prerequisitesValidated", ignore = true)
    @Mapping(target = "capacityValidated", ignore = true)
    @Mapping(target = "creditLimitValidated", ignore = true)
    @Mapping(target = "scheduleConflictChecked", ignore = true)
    @Mapping(target = "validationMessages", ignore = true)
    @Mapping(target = "executionResult", ignore = true)
    @Mapping(target = "assignedBy", ignore = true)
    @Mapping(target = "assignedAt", ignore = true)
    @Mapping(target = "executedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "group", ignore = true)
    ManualAssignment toManualAssignment(ManualAssignmentRequest request);

    // Lists
    List<ManualAssignmentResponse> toManualAssignmentResponseList(List<ManualAssignment> manualAssignments);
}