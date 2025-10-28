package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GroupMapper.class, UserMapper.class})
public interface SystemMapper {

    // SystemConfig Mappings
    SystemConfigResponse toSystemConfigResponse(SystemConfig systemConfig);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "currentRequestPeriodStart", ignore = true)
    @Mapping(target = "currentRequestPeriodEnd", ignore = true)
    void updateSystemConfigFromRequest(SystemConfigRequest request, @MappingTarget SystemConfig systemConfig);

    // AcademicPeriodConfig Mappings
    AcademicPeriodConfigResponse toAcademicPeriodConfigResponse(AcademicPeriodConfig config);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    AcademicPeriodConfig toAcademicPeriodConfig(AcademicPeriodConfigRequest request);

    // Alert Mappings
    @Mapping(source = "group", target = "group")
    @Mapping(source = "resolvedBy", target = "resolvedBy")
    AlertResponse toAlertResponse(Alert alert);

    // Lists
    List<AcademicPeriodConfigResponse> toAcademicPeriodConfigResponseList(List<AcademicPeriodConfig> configs);
    List<AlertResponse> toAlertResponseList(List<Alert> alerts);
}