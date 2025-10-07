package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.ScheduleDto;
import eci.edu.dosw.proyecto.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/current/{studentId}")
    public ResponseEntity<List<ScheduleDto>> getCurrentSchedule(@PathVariable String studentId) {
        return ResponseEntity.ok(scheduleService.getCurrentSchedule(studentId));
    }

    @GetMapping("/past/{studentId}")
    public ResponseEntity<List<ScheduleDto>> getPastSchedules(@PathVariable String studentId) {
        return ResponseEntity.ok(scheduleService.getPastSchedules(studentId));
    }
}
