package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
public interface UserService {
    UserResponse authenticate(LoginRequest loginRequest);
    UserResponse changePassword(ChangePasswordRequest request, String userId);
    UserResponse getUserById(String id);
    UserResponse getUserByUsername(String username);
    List<UserResponse> getUsersByRole(String roleName);
    UserResponse updateUser(String userId, UserResponse userResponse);
    void deactivateUser(String userId);
    long countUsersByRole(String roleName);
    User getCurrentAuthenticatedUser();
    UserResponse assignRole(RoleAssignmentRequest request);
}