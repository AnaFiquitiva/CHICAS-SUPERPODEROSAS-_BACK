package eci.edu.dosw.proyecto.utils;


import eci.edu.dosw.proyecto.dto.StudentCreateDTO;
import eci.edu.dosw.proyecto.dto.StudentDTO;
import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.model.AcademicStatus;

/**
 * Conversor entre la entidad Student y el DTO StudentDTO.
 */
public class StudentMapper {

    public static Student toEntity(StudentDTO dto) {
        if (dto == null) return null;

        Student student = new Student();
        student.setId(dto.getId());
        student.setStudentCode(dto.getStudentCode());
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setInstitutionalEmail(dto.getInstitutionalEmail());
        student.setProgram(dto.getProgram());
        student.setCurrentSemester(dto.getCurrentSemester());
        student.setStatus(AcademicStatus.valueOf(dto.getStatus().toUpperCase()));
        student.setAddress(dto.getAddress());         // mapeo nuevo
        student.setPhoneNumber(dto.getPhoneNumber());
        return student;
    }

    public static Student toEntity(StudentCreateDTO dto) {
        if (dto == null) return null;

        Student student = new Student();
        student.setStudentCode(dto.getStudentCode());
        student.setName(dto.getName());
        student.setInstitutionalEmail(dto.getInstitutionalEmail());
        student.setProgram(dto.getProgram());
        student.setCurrentSemester(dto.getCurrentSemester());
        student.setStatus(AcademicStatus.BLUE);
        student.setAddress(dto.getAddress());         // mapeo nuevo
        student.setPhoneNumber(dto.getPhoneNumber());

        return student;
    }

    public static StudentDTO toDTO(Student entity) {
        if (entity == null) return null;

        StudentDTO dto = new StudentDTO();
        dto.setId(entity.getId());
        dto.setStudentCode(entity.getStudentCode());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setInstitutionalEmail(entity.getInstitutionalEmail());
        dto.setProgram(entity.getProgram());
        dto.setCurrentSemester(entity.getCurrentSemester());
        dto.setStatus(entity.getStatus().name());
        dto.setAddress(entity.getAddress());         // mapeo nuevo
        dto.setPhoneNumber(entity.getPhoneNumber());

        return dto;
    }
}