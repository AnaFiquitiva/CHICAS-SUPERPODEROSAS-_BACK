package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    // Student -> DTO
    @Mapping(source = "program", target = "program")
    @Mapping(source = "trafficLight.color", target = "trafficLightColor")
    StudentResponse toStudentResponse(Student student);

    @Mapping(source = "program.name", target = "programName")
    @Mapping(source = "trafficLight.color", target = "trafficLightColor")
    StudentBasicResponse toStudentBasicResponse(Student student);

    // DTO -> Student
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "trafficLight", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "program", ignore = true)
    @Mapping(target = "personalEmail", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "address", ignore = true)
    Student toStudent(StudentRequest studentRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "institutionalEmail", ignore = true)
    @Mapping(target = "program", ignore = true)
    @Mapping(target = "currentSemester", ignore = true)
    @Mapping(target = "trafficLight", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateStudentFromRequest(StudentUpdateRequest request, @MappingTarget Student student);

    // Lists
    List<StudentResponse> toStudentResponseList(List<Student> students);
    List<StudentBasicResponse> toStudentBasicResponseList(List<Student> students);
}