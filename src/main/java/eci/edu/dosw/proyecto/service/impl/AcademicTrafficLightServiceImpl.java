package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.AcademicTrafficLightResponse;
import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.repository.StudentRepository;
import eci.edu.dosw.proyecto.service.interfaces.AcademicTrafficLightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicTrafficLightServiceImpl implements AcademicTrafficLightService {

    private final StudentRepository studentRepository;

    @Override
    public AcademicTrafficLightResponse getAcademicTrafficLight(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        AcademicTrafficLightResponse response = new AcademicTrafficLightResponse();
        response.setStudentId(student.getId());
        response.setStudentName(student.getName());
        response.setProgram(student.getProgram());
        response.setCurrentSemester(student.getCurrentSemester());

        // Lógica para determinar el semáforo académico
        String status = determineTrafficLightStatus(student);
        response.setStatus(status);

        return response;
    }

    @Override
    public List<AcademicTrafficLightResponse> getTrafficLightByProgram(String program) {
        List<Student> students = studentRepository.findByProgram(program);
        return students.stream()
                .map(student -> getAcademicTrafficLight(student.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AcademicTrafficLightResponse> getTrafficLightByFaculty(String faculty) {
        // Asumiendo que tienes un método para buscar por facultad
        List<Student> students = studentRepository.findAll().stream()
                .filter(student -> student.getFaculty() != null &&
                        student.getFaculty().name().equalsIgnoreCase(faculty))
                .collect(Collectors.toList());

        return students.stream()
                .map(student -> getAcademicTrafficLight(student.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getProgramSummary(String program) {
        List<AcademicTrafficLightResponse> trafficLights = getTrafficLightByProgram(program);

        long greenCount = trafficLights.stream()
                .filter(tl -> "GREEN".equals(tl.getStatus()))
                .count();

        long yellowCount = trafficLights.stream()
                .filter(tl -> "YELLOW".equals(tl.getStatus()))
                .count();

        long redCount = trafficLights.stream()
                .filter(tl -> "RED".equals(tl.getStatus()))
                .count();

        Map<String, Object> summary = new HashMap<>();
        summary.put("program", program);
        summary.put("totalStudents", trafficLights.size());
        summary.put("greenCount", greenCount);
        summary.put("yellowCount", yellowCount);
        summary.put("redCount", redCount);
        summary.put("successRate", trafficLights.size() > 0 ?
                (double) greenCount / trafficLights.size() * 100 : 0.0);

        return summary;
    }

    private String determineTrafficLightStatus(Student student) {
        // Lógica simplificada para determinar el estado del semáforo
        // Debes adaptar esto según tu lógica de negocio real
        if (student.getStatus() != null) {
            switch (student.getStatus()) {
                case GREEN:
                    return "GREEN";
                case BLUE:
                    return "YELLOW";
                case RED:
                    return "RED";
                default:
                    return "YELLOW";
            }
        }

        // Lógica alternativa basada en semestre y rendimiento
        if (student.getCurrentSemester() != null) {
            if (student.getCurrentSemester() <= 4) {
                return "GREEN";
            } else if (student.getCurrentSemester() <= 8) {
                return "YELLOW";
            } else {
                return "RED";
            }
        }

        return "YELLOW";
    }
}