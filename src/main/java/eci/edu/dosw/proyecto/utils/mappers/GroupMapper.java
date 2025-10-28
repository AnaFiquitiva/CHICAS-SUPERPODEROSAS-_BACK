package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    // Group Mappings
    @Mapping(source = "subject", target = "subject")
    @Mapping(source = "professor", target = "professor")
    @Mapping(source = "schedules", target = "schedules")
    @Mapping(expression = "java(group.getOccupancyPercentage())", target = "occupancyPercentage")
    @Mapping(expression = "java(group.hasAvailableSpots())", target = "hasAvailableSpots")
    GroupResponse toGroupResponse(Group group);

    @Mapping(source = "subject.name", target = "subjectName")
    @Mapping(source = "subject.code", target = "subjectCode")
    @Mapping(expression = "java(group.getOccupancyPercentage())", target = "occupancyPercentage")
    @Mapping(expression = "java(group.hasAvailableSpots())", target = "hasAvailableSpots")
    GroupBasicResponse toGroupBasicResponse(Group group);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentEnrollment", ignore = true)
    @Mapping(target = "totalRequests", ignore = true)
    @Mapping(target = "approvedRequests", ignore = true)
    @Mapping(target = "rejectedRequests", ignore = true)
    @Mapping(target = "pendingRequests", ignore = true)
    @Mapping(target = "lastOccupancyAlert", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "professor", ignore = true)
    Group toGroup(GroupRequest groupRequest);

    // ✅ MÉTODO AGREGADO: updateGroupFromRequest
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groupCode", ignore = true)
    @Mapping(target = "currentEnrollment", ignore = true)
    @Mapping(target = "totalRequests", ignore = true)
    @Mapping(target = "approvedRequests", ignore = true)
    @Mapping(target = "rejectedRequests", ignore = true)
    @Mapping(target = "pendingRequests", ignore = true)
    @Mapping(target = "lastOccupancyAlert", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "subject", ignore = true)
    void updateGroupFromRequest(GroupUpdateRequest request, @MappingTarget Group group);

    @Mapping(source = "groupCode", target = "groupCode")
    @Mapping(source = "subject.name", target = "subjectName")
    @Mapping(expression = "java(group.getMaxCapacity() - group.getCurrentEnrollment())", target = "availableSpots")
    @Mapping(expression = "java(group.getOccupancyPercentage())", target = "occupancyPercentage")
    @Mapping(expression = "java(group.hasAvailableSpots())", target = "hasAvailableSpots")
    GroupCapacityResponse toGroupCapacityResponse(Group group);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "group", ignore = true)
    Schedule toSchedule(ScheduleRequest scheduleRequest);

    ScheduleResponse toScheduleResponse(Schedule schedule);

    // Lists
    List<GroupResponse> toGroupResponseList(List<Group> groups);
    List<GroupBasicResponse> toGroupBasicResponseList(List<Group> groups);
    List<ScheduleResponse> toScheduleResponseList(List<Schedule> schedules);
}