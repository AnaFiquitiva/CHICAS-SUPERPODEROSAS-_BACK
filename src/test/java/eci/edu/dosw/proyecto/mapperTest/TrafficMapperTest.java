package eci.edu.dosw.proyecto.mapperTest;

import eci.edu.dosw.proyecto.dto.TrafficLightDto;
import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.utils.TrafficLightMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrafficLightMapperTest {

    private final TrafficLightMapper mapper = new TrafficLightMapper();

    @Test
    void shouldMapStudentToTrafficLightDtoCorrectly() {
        Student student = new Student();
        student.setId("1");
        student.setName("John Doe");
        TrafficLightDto dto = mapper.toDto(student, 4.5, "GREEN");
        assertNotNull(dto);
        assertTrue(dto.getAverage() >= 0);
        assertNotNull(dto.getStatus());
    }
}