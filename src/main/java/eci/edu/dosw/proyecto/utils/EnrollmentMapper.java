package eci.edu.dosw.proyecto.utils;

import eci.edu.dosw.proyecto.dto.EnrollmentRequestDTO;
import eci.edu.dosw.proyecto.dto.EnrollmentResponseDTO;
import eci.edu.dosw.proyecto.model.Enrollment;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.model.Subject;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EnrollmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subjectId", ignore = true)
    @Mapping(target = "enrollmentDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    Enrollment toEntity(EnrollmentRequestDTO dto);

    @Mapping(target = "subjectName", source = "subject.name")
    @Mapping(target = "groupCode", source = "group.groupCode")
    EnrollmentResponseDTO toResponseDTO(Enrollment enrollment, Subject subject, Group group);

    @Mapping(target = "subjectName", source = "subject.name")
    @Mapping(target = "groupCode", source = "group.groupCode")
    @Mapping(target = "message", ignore = true)
    EnrollmentResponseDTO toResponseDTOWithMessage(Enrollment enrollment, Subject subject, Group group, String message);
}