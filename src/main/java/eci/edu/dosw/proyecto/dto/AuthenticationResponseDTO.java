package eci.edu.dosw.proyecto.dto;

public class AuthenticationResponseDTO {
    private String userId;
    private String email;
    private String role;
    private boolean success;
    private String message;

    public AuthenticationResponseDTO() {}

    public AuthenticationResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public AuthenticationResponseDTO(String userId, String email, String role, boolean success, String message) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.success = success;
        this.message = message;
    }

    // Getters y Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}