package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.service.interfaces.GroupService;
import eci.edu.dosw.proyecto.service.interfaces.WaitingListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Grupos", description = "Gestión de grupos académicos")
public class GroupController {

    private final GroupService groupService;
    private final WaitingListService waitingListService;

    // === CRUD BÁSICO ===

    @Operation(summary = "Crear grupo")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody GroupRequest groupRequest) {
        GroupResponse group = groupService.createGroup(groupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @Operation(summary = "Obtener grupo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroupById(@PathVariable String id) {
        GroupResponse group = groupService.getGroupById(id);
        return ResponseEntity.ok(group);
    }

    @Operation(summary = "Listar grupos por materia")
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<GroupResponse>> getGroupsBySubject(@PathVariable String subjectId) {
        List<GroupResponse> groups = groupService.getGroupsBySubject(subjectId);
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "Listar grupos por facultad")
    @GetMapping("/faculty/{facultyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<GroupResponse>> getGroupsByFaculty(@PathVariable String facultyId) {
        List<GroupResponse> groups = groupService.getGroupsByFaculty(facultyId);
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "Listar todos los grupos activos")
    @GetMapping
    public ResponseEntity<List<GroupResponse>> getAllActiveGroups() {
        List<GroupResponse> groups = groupService.getAllActiveGroups();
        return ResponseEntity.ok(groups);
    }

    // === CAPACIDAD Y OCUPACIÓN ===

    @Operation(summary = "Obtener capacidad del grupo")
    @GetMapping("/{id}/capacity")
    public ResponseEntity<GroupCapacityResponse> getGroupCapacity(@PathVariable String id) {
        GroupCapacityResponse capacity = groupService.getGroupCapacity(id);
        return ResponseEntity.ok(capacity);
    }

    @Operation(summary = "Listar grupos con alta ocupación")
    @GetMapping("/high-occupancy")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GroupResponse>> getGroupsAtHighOccupancy(
            @Parameter(description = "Umbral de ocupación en porcentaje (por defecto 90%)")
            @RequestParam(defaultValue = "90.0") Double threshold) {
        List<GroupResponse> groups = groupService.getGroupsAtHighOccupancy(threshold);
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "Listar grupos con alta ocupación (detallado)")
    @GetMapping("/high-occupancy/detailed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GroupOccupancyResponse>> getGroupsAtHighOccupancyDetailed(
            @Parameter(description = "Umbral de ocupación en porcentaje (por defecto 90%)")
            @RequestParam(defaultValue = "90.0") Double threshold) {
        List<GroupOccupancyResponse> groups = groupService.getGroupsAtHighOccupancyWithDetails(threshold);
        return ResponseEntity.ok(groups);
    }

    // === LISTA DE ESPERA ===

    @Operation(summary = "Agregar estudiante a lista de espera")
    @PostMapping("/{groupId}/waiting-list")
    public ResponseEntity<WaitingListResponse> addStudentToWaitingList(@PathVariable String groupId, @RequestBody WaitingListAddRequest addRequest) {
        WaitingListResponse waitingList = waitingListService.addStudentToWaitingList(groupId, addRequest.getStudentId());
        return ResponseEntity.ok(waitingList);
    }

    @Operation(summary = "Obtener lista de espera del grupo")
    @GetMapping("/{groupId}/waiting-list")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<WaitingListResponse> getWaitingListByGroup(@PathVariable String groupId) {
        WaitingListResponse waitingList = waitingListService.getWaitingListByGroup(groupId);
        return ResponseEntity.ok(waitingList);
    }

    @Operation(summary = "Promover siguiente estudiante de lista de espera")
    @PostMapping("/{groupId}/waiting-list/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> promoteNextStudent(@PathVariable String groupId) {
        boolean promoted = waitingListService.promoteNextStudent(groupId);
        return ResponseEntity.ok(promoted);
    }
    @Operation(summary = "Modificar cupo de un grupo")
    @PatchMapping("/{groupId}/capacity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GroupResponse> updateGroupCapacity(
            @Parameter(description = "ID del grupo", required = true)
            @PathVariable String groupId,
            @Valid @RequestBody
            @Parameter(description = "Nueva capacidad y justificación", required = true)
            GroupCapacityUpdateRequest capacityRequest) {
        GroupResponse group = groupService.updateGroupCapacity(groupId, capacityRequest);
        return ResponseEntity.ok(group);
    }
    // eci.edu.dosw.proyecto.controller.GroupController.java
    @Operation(summary = "Asignar profesor a grupo")
    @PostMapping("/{groupId}/professor")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<GroupResponse> assignProfessorToGroup(
            @PathVariable String groupId,
            @RequestBody ProfessorAssignmentRequest assignmentRequest) {
        GroupResponse group = groupService.assignProfessorToGroup(groupId, assignmentRequest.getProfessorId());
        return ResponseEntity.ok(group);
    }

    @Operation(summary = "Retirar profesor de grupo")
    @DeleteMapping("/{groupId}/professor")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<GroupResponse> removeProfessorFromGroup(@PathVariable String groupId) {
        GroupResponse group = groupService.removeProfessorFromGroup(groupId);
        return ResponseEntity.ok(group);
    }
    // eci.edu.dosw.proyecto.controller.GroupController.java
    @Operation(summary = "Agregar horario a grupo")
    @PostMapping("/schedules")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<ScheduleResponse> addSchedule(@Valid @RequestBody ScheduleRequest scheduleRequest) {
        ScheduleResponse schedule = groupService.addSchedule(scheduleRequest);
        return ResponseEntity.ok(schedule);
    }

    @Operation(summary = "Actualizar horario")
    @PutMapping("/schedules/{scheduleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @PathVariable String scheduleId,
            @Valid @RequestBody ScheduleRequest scheduleRequest) {
        ScheduleResponse schedule = groupService.updateSchedule(scheduleId, scheduleRequest);
        return ResponseEntity.ok(schedule);
    }

    @Operation(summary = "Eliminar horario")
    @DeleteMapping("/schedules/{scheduleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<Void> removeSchedule(@PathVariable String scheduleId) {
        groupService.removeSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar todos los horarios del grupo")
    @DeleteMapping("/{groupId}/schedules/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<Void> removeAllSchedules(@PathVariable String groupId) {
        groupService.removeAllSchedules(groupId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener horarios del grupo")
    @GetMapping("/{groupId}/schedules")
    public ResponseEntity<List<ScheduleResponse>> getGroupSchedules(@PathVariable String groupId) {
        List<ScheduleResponse> schedules = groupService.getGroupSchedules(groupId);
        return ResponseEntity.ok(schedules);
    }
}
