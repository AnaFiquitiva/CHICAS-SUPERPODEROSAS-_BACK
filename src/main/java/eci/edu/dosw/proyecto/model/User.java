package eci.edu.dosw.proyecto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Clase que representa a un usuario del sistema académico.
 * Contiene la información básica de autenticación y roles.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Field("username")
    private String username;

    @Field("password")
    private String password;

    @Field("email")
    private String email;

    @DBRef
    private Role role;

    private boolean active;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
