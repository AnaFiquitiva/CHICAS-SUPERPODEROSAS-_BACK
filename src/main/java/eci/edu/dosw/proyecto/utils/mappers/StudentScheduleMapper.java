package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.GroupBasicResponse;
import eci.edu.dosw.proyecto.dto.ScheduleResponse;
import eci.edu.dosw.proyecto.dto.StudentBasicResponse;
import eci.edu.dosw.proyecto.dto.StudentScheduleResponse;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.model.Schedule;
import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.model.StudentSchedule;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentScheduleMapper {

    public StudentScheduleResponse toStudentScheduleResponse(StudentSchedule studentSchedule) {
        if (studentSchedule == null) {
            return null;
        }

        StudentScheduleResponse response = new StudentScheduleResponse();
        response.setId(studentSchedule.getId());
        response.setStudent(toStudentBasicResponse(studentSchedule.getStudent()));
        response.setAcademicPeriod(studentSchedule.getAcademicPeriod());
        response.setAcademicYear(studentSchedule.getAcademicYear());
        response.setCreatedAt(studentSchedule.getCreatedAt());

        // Mapear grupos inscritos
        if (studentSchedule.getEnrolledGroups() != null) {
            response.setEnrolledGroups(
                    studentSchedule.getEnrolledGroups().stream()
                            .map(this::toGroupBasicResponse)
                            .collect(Collectors.toList())
            );
        }

        // Mapear horarios
        response.setSchedules(getSchedulesFromGroups(studentSchedule.getEnrolledGroups()));

        return response;
    }

    public List<StudentScheduleResponse> toStudentScheduleResponseList(List<StudentSchedule> studentSchedules) {
        return studentSchedules.stream()
                .map(this::toStudentScheduleResponse)
                .collect(Collectors.toList());
    }

    // === MÉTODOS AUXILIARES DE MAPEO ===

    private StudentBasicResponse toStudentBasicResponse(Student student) {
        if (student == null) {
            return null;
        }

        StudentBasicResponse response = new StudentBasicResponse();
        response.setId(student.getId());
        response.setCode(student.getCode());
        response.setFirstName(student.getFirstName());
        response.setLastName(student.getLastName());
        response.setInstitutionalEmail(student.getInstitutionalEmail());

        if (student.getProgram() != null) {
            response.setProgramName(student.getProgram().getName());
        }
        response.setCurrentSemester(student.getCurrentSemester());

        if (student.getTrafficLight() != null) {
            response.setTrafficLightColor(student.getTrafficLight().getColor());
        }

        return response;
    }

    private GroupBasicResponse toGroupBasicResponse(Group group) {
        if (group == null) {
            return null;
        }

        GroupBasicResponse response = new GroupBasicResponse();
        response.setId(group.getId());
        response.setGroupCode(group.getGroupCode());

        if (group.getSubject() != null) {
            response.setSubjectName(group.getSubject().getName());
            response.setSubjectCode(group.getSubject().getCode());
        }

        response.setMaxCapacity(group.getMaxCapacity());
        response.setCurrentEnrollment(group.getCurrentEnrollment());
        response.setOccupancyPercentage(group.getOccupancyPercentage());
        response.setHasAvailableSpots(group.hasAvailableSpots());

        return response;
    }

    private List<ScheduleResponse> getSchedulesFromGroups(List<Group> groups) {
        if (groups == null) {
            return List.of();
        }

        return groups.stream()
                .flatMap(group -> group.getSchedules().stream())
                .map(this::toScheduleResponse)
                .collect(Collectors.toList());
    }

    private ScheduleResponse toScheduleResponse(Schedule schedule) {
        if (schedule == null) {
            return null;
        }

        ScheduleResponse response = new ScheduleResponse();
        response.setId(schedule.getId());
        response.setDayOfWeek(schedule.getDayOfWeek());
        response.setStartTime(schedule.getStartTime());
        response.setEndTime(schedule.getEndTime());
        response.setClassroom(schedule.getClassroom());

        if (schedule.getGroup() != null) {
            response.setGroupId(schedule.getGroup().getId());
        }

        return response;
    }
}