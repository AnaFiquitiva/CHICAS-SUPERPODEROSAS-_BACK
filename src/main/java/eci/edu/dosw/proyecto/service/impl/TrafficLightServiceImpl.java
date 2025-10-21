package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.TrafficLightDto;
import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.repository.StudentRepository;
import eci.edu.dosw.proyecto.service.interfaces.TrafficLightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrafficLightServiceImpl implements TrafficLightService {

    private final StudentRepository studentRepository;

    @Override
    public TrafficLightDto getStudentTrafficLight(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        double average = calculateStudentAverage(student);
        String status = determineStatus(average);

        return TrafficLightDto.builder()
                .studentCode(student.getStudentCode())
                .name(student.getName())
                .average(average)
                .status(status)
                .build();
    }

    private double calculateStudentAverage(Student student) {
        // TODO: Replace with real logic using student enrollments or academic plans
        return Math.random() * 5; // Simulates GPA between 0 and 5
    }

    private String determineStatus(double average) {
        if (average >= 4.0) return "GREEN";
        else if (average >= 3.0) return "YELLOW";
        else return "RED";
    }
}
