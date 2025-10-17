package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.AcademicTrafficLightResponse;
import eci.edu.dosw.proyecto.service.interfaces.AcademicTrafficLightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/traffic-light")
@RequiredArgsConstructor
public class AcademicTrafficLightController {

    private final AcademicTrafficLightService trafficLightService;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<AcademicTrafficLightResponse> getTrafficLight(@PathVariable String studentId) {
        return ResponseEntity.ok(trafficLightService.getAcademicTrafficLight(studentId));
    }

    @GetMapping("/program/{program}")
    public ResponseEntity<List<AcademicTrafficLightResponse>> getByProgram(@PathVariable String program) {
        return ResponseEntity.ok(trafficLightService.getTrafficLightByProgram(program));
    }

    @GetMapping("/faculty/{faculty}")
    public ResponseEntity<List<AcademicTrafficLightResponse>> getByFaculty(@PathVariable String faculty) {
        return ResponseEntity.ok(trafficLightService.getTrafficLightByFaculty(faculty));
    }

    @GetMapping("/program/{program}/summary")
    public ResponseEntity<Map<String, Object>> getProgramSummary(@PathVariable String program) {
        return ResponseEntity.ok(trafficLightService.getProgramSummary(program));
    }
}