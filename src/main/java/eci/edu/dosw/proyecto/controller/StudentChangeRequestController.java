package eci.edu.dosw.proyecto.controller;
import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador REST para gestión de solicitudes de cambio por estudiantes
 */
@RestController
@RequestMapping("/api/students/{studentId}/change-requests")
@RequiredArgsConstructor
public class StudentChangeRequestController {

    private final ChangeRequestService changeRequestService;

    @PostMapping
    public ResponseEntity<ChangeRequestResponseDTO> createChangeRequest(
            @PathVariable String studentId,
            @Valid @RequestBody ChangeRequestDTO changeRequestDTO) {

        changeRequestDTO.setStudentId(studentId);
        var changeRequest = changeRequestService.createChangeRequest(changeRequestDTO);
        return ResponseEntity.ok(changeRequestService.mapToResponseDTO(changeRequest));
    }

    @GetMapping
    public ResponseEntity<List<ChangeRequestResponseDTO>> getStudentRequests(
            @PathVariable String studentId) {

        return ResponseEntity.ok(changeRequestService.getStudentRequests(studentId));
    }

    @GetMapping("/{trackingNumber}")
    public ResponseEntity<ChangeRequestResponseDTO> getRequestByTrackingNumber(
            @PathVariable String studentId,
            @PathVariable String trackingNumber) {

        return ResponseEntity.ok(changeRequestService.getRequestByTrackingNumber(trackingNumber));
    }
}
