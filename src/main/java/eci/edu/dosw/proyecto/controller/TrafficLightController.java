package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.TrafficLightDto;
import eci.edu.dosw.proyecto.service.interfaces.TrafficLightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/traffic-light")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TrafficLightController {

    private final TrafficLightService trafficLightService;

    @GetMapping("/{studentId}")
    public ResponseEntity<TrafficLightDto> getStudentTrafficLight(@PathVariable String studentId) {
        return ResponseEntity.ok(trafficLightService.getStudentTrafficLight(studentId));
    }
}
