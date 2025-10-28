package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para la clase Dean")
class DeanTest {

    private LocalDateTime now;
    private Faculty mockFaculty;
    private User mockUser;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        mockFaculty = Faculty.builder().id("fac-1").name("Ingeniería").build();
        mockUser = User.builder().id("user-dean").build();
    }

    @Test
    @DisplayName("Debería crear un Dean correctamente usando el patrón Builder")
    void shouldCreateDeanUsingBuilder_happyPath() {
        // Arrange
        String expectedId = "dean-789";
        String expectedName = "Juan Decano";

        // Act
        Dean dean = Dean.builder()
                .id(expectedId)
                .firstName("Juan")
                .lastName("Decano")
                .institutionalEmail("juan.decano@uni.edu")
                .faculty(mockFaculty)
                .active(true)
                .user(mockUser)
                .createdAt(now)
                .build();

        // Assert
        assertThat(dean).isNotNull();
        assertThat(dean.getId()).isEqualTo(expectedId);
        assertThat(dean.getFirstName()).isEqualTo("Juan");
        assertThat(dean.getLastName()).isEqualTo("Decano");
        assertThat(dean.getInstitutionalEmail()).isEqualTo("juan.decano@uni.edu");
        assertThat(dean.getFaculty()).isEqualTo(mockFaculty);
        assertThat(dean.isActive()).isTrue();
        assertThat(dean.getUser()).isEqualTo(mockUser);
    }

    @Test
    @DisplayName("Debería crear un Dean con valores por defecto usando el constructor vacío")
    void shouldCreateDeanWithDefaultValues_happyPath() {
        // Act
        Dean dean = new Dean();

        // Assert
        assertThat(dean).isNotNull();
        assertThat(dean.getId()).isNull();
        assertThat(dean.getFirstName()).isNull();
        assertThat(dean.isActive()).isFalse();
        assertThat(dean.getFaculty()).isNull();
    }

    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode() para objetos iguales")
    void shouldVerifyEqualsAndHashCodeContract_happyPath() {
        // Arrange
        Dean dean1 = Dean.builder()
                .id("dean-xyz")
                .firstName("Ana")
                .lastName("Decana")
                .build();

        Dean dean2 = Dean.builder()
                .id("dean-xyz")
                .firstName("Ana")
                .lastName("Decana")
                .build();

        Dean dean3 = Dean.builder()
                .id("dean-abc")
                .build();

        // Act & Assert
        assertThat(dean1).isEqualTo(dean2);
        assertThat(dean1.hashCode()).isEqualTo(dean2.hashCode());
        assertThat(dean1).isNotEqualTo(dean3);
    }
}