package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.AcademicProgressResponse;
import eci.edu.dosw.proyecto.service.interfaces.AcademicProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/academic-progress")
@RequiredArgsConstructor
public class AcademicProgressController {

    private final AcademicProgressService academicProgressService;

    @GetMapping("/student/{studentCode}")
    public ResponseEntity<AcademicProgressResponse> getByStudentCode(@PathVariable String studentCode) {
        return ResponseEntity.ok(academicProgressService.getAcademicProgress(studentCode));
    }

    @GetMapping("/student/id/{studentId}")
    public ResponseEntity<AcademicProgressResponse> getByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(academicProgressService.getAcademicProgressByStudentId(studentId));
    }
}