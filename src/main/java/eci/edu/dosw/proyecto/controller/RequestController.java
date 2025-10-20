package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.model.Request;
import eci.edu.dosw.proyecto.model.RequestStatus;
import eci.edu.dosw.proyecto.service.interfaces.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    // --- Consultar todas las solicitudes de una facultad ---
    @GetMapping("/faculty/{faculty}")
    public ResponseEntity<List<Request>> getRequestsByFaculty(@PathVariable String faculty) {
        return ResponseEntity.ok(requestService.getRequestsByFaculty(faculty));
    }

    // --- Consultar solicitudes filtradas por estado ---
    @GetMapping("/faculty/{faculty}/status/{status}")
    public ResponseEntity<List<Request>> getRequestsByFacultyAndStatus(
            @PathVariable String faculty,
            @PathVariable RequestStatus status) {
        return ResponseEntity.ok(requestService.getRequestsByFacultyAndStatus(faculty, status));
    }

    // --- Responder solicitud (solo decanatura) ---
    @PostMapping("/{requestId}/respond")
    public ResponseEntity<Request> respondRequest(
            @PathVariable String requestId,
            @RequestParam String action,
            @RequestParam(required = false) String comment,
            @RequestParam String deanFaculty) {

        return ResponseEntity.ok(requestService.respondRequest(requestId, action, comment, deanFaculty));
    }

    // --- Actualizar información adicional (solo estudiante) ---
    @PutMapping("/{requestId}/update-info")
    public ResponseEntity<Request> updateRequestWithInfo(
            @PathVariable String requestId,
            @RequestParam String studentId,
            @RequestParam String comment) {

        return ResponseEntity.ok(requestService.updateRequestWithInfo(requestId, studentId, comment));
    }
}
