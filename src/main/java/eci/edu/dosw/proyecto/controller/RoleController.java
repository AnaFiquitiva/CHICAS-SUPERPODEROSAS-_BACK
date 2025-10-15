package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@RequestBody RoleDto roleDto) {
        return ResponseEntity.ok(roleService.createRole(roleDto));
    }

    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignRole(@RequestBody RoleAssignmentRequest request) {
        return ResponseEntity.ok(roleService.assignRole(request));
    }

    @PostMapping("/transfer-admin")
    public ResponseEntity<String> transferAdminRole(@RequestBody RoleTransferRequest request) {
        return ResponseEntity.ok(roleService.transferAdminRole(
                request.getCurrentAdminId(), request.getTargetUserId()));
    }

    @GetMapping("/check-admin")
    public ResponseEntity<Boolean> hasActiveAdmin() {
        return ResponseEntity.ok(roleService.hasActiveAdmin());
    }
}
