package eci.edu.dosw.proyecto.service;

import eci.edu.dosw.proyecto.dto.*;
import java.util.List;

public interface RoleService {
    RoleDto createRole(RoleDto roleDto);
    List<RoleDto> getAllRoles();
    String assignRole(RoleAssignmentRequest request);
    String transferAdminRole(String currentAdminId, String targetUserId);
    boolean hasActiveAdmin();
}
