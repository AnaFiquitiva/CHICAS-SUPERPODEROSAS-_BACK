package eci.edu.dosw.proyecto.dto;

public class UserAuthenticationDTO {
    private String userId;
    private String password;

    public UserAuthenticationDTO() {}

    public UserAuthenticationDTO(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    // Getters y Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}