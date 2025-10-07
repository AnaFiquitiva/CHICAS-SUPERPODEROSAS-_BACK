package eci.edu.dosw.proyecto.mapperTest;

import eci.edu.dosw.proyecto.dto.ScheduleDto;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.utils.mappers.ScheduleMapper;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleMapperTest {

    private final ScheduleMapper scheduleMapper = new ScheduleMapper();

    @Test
    void shouldMapScheduleToDtoCorrectly() {
        Course course = new Course();
        course.setName("Databases");

        Classroom classroom = new Classroom();
        classroom.setBuilding("Building B");
        classroom.setRoomNumber("101");

        Semester semester = new Semester();
        semester.setCode("2025-1");

        Schedule schedule = new Schedule();
        schedule.setCourse(course);
        schedule.setClassroom(classroom);
        schedule.setSemester(semester);
        schedule.setDay("Wednesday");
        schedule.setStartTime(LocalTime.of(10, 0));
        schedule.setEndTime(LocalTime.of(12, 0));

        ScheduleDto dto = scheduleMapper.toDto(schedule);

        assertEquals("Databases", dto.getCourseName());
        assertEquals("Building B 101", dto.getClassroom());
        assertEquals("Wednesday", dto.getDay());
        assertEquals("2025-1", dto.getSemesterCode());
    }
}