package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.ScheduleDto;
import eci.edu.dosw.proyecto.dto.StudentDTO;
import eci.edu.dosw.proyecto.model.Schedule;
import eci.edu.dosw.proyecto.model.Student;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScheduleMapper {

    public ScheduleDto toDto(Schedule schedule) {
        if (schedule == null) return null;

        return ScheduleDto.builder()
                .courseName(schedule.getCourse() != null ? schedule.getCourse().getName() : null)
                .day(schedule.getDay())
                .startTime(schedule.getStartTime() != null ? schedule.getStartTime().toString() : null)
                .endTime(schedule.getEndTime() != null ? schedule.getEndTime().toString() : null)
                .classroom(schedule.getClassroom() != null
                        ? schedule.getClassroom().getBuilding() + " " + schedule.getClassroom().getRoomNumber()
                        : null)
                .semesterCode(schedule.getSemester() != null ? schedule.getSemester().getCode() : null)
                .build();
    }

    public List<ScheduleDto> toDtoList(List<Schedule> schedules) {
        return schedules.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public StudentDTO toStudentDto(Student student) {
        if (student == null) return null;

        return StudentDTO.builder()
                .id(student.getId())
                .studentCode(student.getStudentCode()) // 🔹 cambiado getCode() → getStudentCode()
                .name(student.getName())
                .email(student.getEmail())
                .institutionalEmail(student.getInstitutionalEmail())
                .program(student.getProgram())
                .currentSemester(student.getCurrentSemester())
                .status(student.getStatus() != null ? student.getStatus().name() : null)
                .address(student.getAddress())
                .phoneNumber(student.getPhoneNumber())
                .build();
    }

    public List<StudentDTO> toStudentDtoList(List<Student> students) {
        return students.stream()
                .map(this::toStudentDto)
                .collect(Collectors.toList());
    }
}

