package eci.edu.dosw.proyecto.repositoryTest;

import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class ScheduleRepositoryTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    void shouldFindByStudentIdAndCurrentSemester() {

        Student student = Student.builder().id("123").name("Juan").build();
        Semester semester = Semester.builder().id("2025-1").code("2025-1").isCurrent(true).build();

        Schedule schedule = Schedule.builder()
                .day("Monday")
                .student(student)
                .semester(semester)
                .build();

        scheduleRepository.save(schedule);


        List<Schedule> result = scheduleRepository.findByStudent_IdAndSemester_IsCurrent("123", true);

        assertEquals(1, result.size());
        assertEquals("123", result.get(0).getStudent().getId());
        assertTrue(result.get(0).getSemester().isCurrent());
    }
}

