package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.GroupBasicResponse;
import eci.edu.dosw.proyecto.dto.StudentBasicResponse;
import eci.edu.dosw.proyecto.dto.WaitingListEntryResponse;
import eci.edu.dosw.proyecto.dto.WaitingListResponse;
import eci.edu.dosw.proyecto.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WaitingListMapper {

    @Mapping(source = "group", target = "group")
    WaitingListResponse toWaitingListResponse(WaitingList waitingList);

    @Mapping(source = "student", target = "student")
    @Mapping(source = "joinedAt", target = "joinedAt")

    WaitingListEntryResponse toWaitingListEntryResponse(WaitingListEntry entry);

    // Métodos auxiliares
    @Mapping(source = "id", target = "id")
    @Mapping(source = "groupCode", target = "groupCode")
    @Mapping(source = "subject.name", target = "subjectName")
    @Mapping(source = "subject.code", target = "subjectCode")
    @Mapping(source = "maxCapacity", target = "maxCapacity")
    @Mapping(source = "currentEnrollment", target = "currentEnrollment")
    @Mapping(expression = "java(group.getOccupancyPercentage())", target = "occupancyPercentage")
    @Mapping(expression = "java(group.hasAvailableSpots())", target = "hasAvailableSpots")
    GroupBasicResponse toGroupBasicResponse(Group group);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "institutionalEmail", target = "institutionalEmail")
    @Mapping(source = "program.name", target = "programName")
    @Mapping(source = "currentSemester", target = "currentSemester")
    @Mapping(source = "trafficLight.color", target = "trafficLightColor")
    StudentBasicResponse toStudentBasicResponse(Student student);
}