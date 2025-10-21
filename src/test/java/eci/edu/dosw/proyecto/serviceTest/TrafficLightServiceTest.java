package eci.edu.dosw.proyecto.serviceTest;

import eci.edu.dosw.proyecto.dto.TrafficLightDto;
import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.repository.StudentRepository;
import eci.edu.dosw.proyecto.service.impl.TrafficLightServiceImpl;
import eci.edu.dosw.proyecto.utils.TrafficLightMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrafficLightServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TrafficLightMapper trafficLightMapper;

    @InjectMocks
    private TrafficLightServiceImpl trafficLightService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnGreenTrafficLightForHighAverage() {
        Student student = new Student();
        student.setId("1");
        student.setName("John Doe");

        when(studentRepository.findById("1")).thenReturn(Optional.of(student));
        when(trafficLightMapper.toDto(student, 4.5, "GREEN"));

        TrafficLightDto result = trafficLightService.getStudentTrafficLight("1");

        assertEquals("GREEN", result.getStatus());
        assertEquals(4.5, result.getAverage());
        verify(studentRepository, times(1)).findById("1");
    }

    @Test
    void shouldThrowErrorIfStudentNotFound() {
        when(studentRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            trafficLightService.getStudentTrafficLight("99");
        });

        verify(studentRepository, times(1)).findById("99");
    }
}
