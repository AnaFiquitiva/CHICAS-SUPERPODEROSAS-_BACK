package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.CourseDetailDto;
import eci.edu.dosw.proyecto.service.interfaces.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/search")
    public ResponseEntity<List<CourseDetailDto>> searchCourses(@RequestParam String query) {
        return ResponseEntity.ok(courseService.searchCourses(query));
    }
}
