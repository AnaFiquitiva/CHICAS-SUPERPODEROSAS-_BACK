package eci.edu.dosw.proyecto.utils;

import eci.edu.dosw.proyecto.dto.TrafficLightDto;
import eci.edu.dosw.proyecto.model.Student;
import org.springframework.stereotype.Component;

@Component
public class TrafficLightMapper {

    public TrafficLightDto toDto(Student student, double average, String status) {
        return TrafficLightDto.builder()
                .studentCode(student.getStudentCode())
                .name(student.getName())
                .average(average)
                .status(status)
                .build();
    }
}
