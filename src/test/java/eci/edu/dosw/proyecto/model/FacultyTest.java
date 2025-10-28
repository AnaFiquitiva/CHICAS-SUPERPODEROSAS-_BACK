package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para la clase Faculty")
class FacultyTest {

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("Debería crear una Faculty correctamente usando el patrón Builder")
    void shouldCreateFacultyUsingBuilder_happyPath() {
        // Arrange
        String expectedId = "fac-123";
        String expectedCode = "FING";
        String expectedName = "Facultad de Ingeniería";

        // Act
        Faculty faculty = Faculty.builder()
                .id(expectedId)
                .code(expectedCode)
                .name(expectedName)
                .description("La mejor facultad")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertThat(faculty).isNotNull();
        assertThat(faculty.getId()).isEqualTo(expectedId);
        assertThat(faculty.getCode()).isEqualTo(expectedCode);
        assertThat(faculty.getName()).isEqualTo(expectedName);
        assertThat(faculty.isActive()).isTrue();
    }

    @Test
    @DisplayName("Debería crear una Faculty con valores por defecto usando el constructor vacío")
    void shouldCreateFacultyWithDefaultValues_happyPath() {
        // Act
        Faculty faculty = new Faculty();

        // Assert
        assertThat(faculty).isNotNull();
        assertThat(faculty.getId()).isNull();
        assertThat(faculty.getCode()).isNull();
        assertThat(faculty.isActive()).isFalse();
    }

    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode() para objetos iguales")
    void shouldVerifyEqualsAndHashCodeContract_happyPath() {
        // Arrange
        Faculty faculty1 = Faculty.builder()
                .id("fac-xyz")
                .code("FCE")
                .name("Facultad de Ciencias Económicas")
                .build();

        Faculty faculty2 = Faculty.builder()
                .id("fac-xyz")
                .code("FCE")
                .name("Facultad de Ciencias Económicas")
                .build();

        Faculty faculty3 = Faculty.builder()
                .id("fac-abc")
                .build();

        // Act & Assert
        assertThat(faculty1).isEqualTo(faculty2);
        assertThat(faculty1.hashCode()).isEqualTo(faculty2.hashCode());
        assertThat(faculty1).isNotEqualTo(faculty3);
    }
}