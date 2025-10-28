package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para la clase AcademicPeriodConfig")
class AcademicPeriodConfigTest {

    private LocalDateTime now;
    private LocalDateTime oneHourAgo;
    private LocalDateTime oneHourLater;
    private Faculty mockFaculty;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // Usamos una referencia de tiempo fija para que las pruebas sean deterministas
        now = LocalDateTime.now();
        oneHourAgo = now.minusHours(1);
        oneHourLater = now.plusHours(1);

        // Creamos instancias simples para las referencias @DBRef
        mockFaculty = Faculty.builder().id("fac-1").name("Ingeniería").build();
        mockUser = User.builder().id("user-1").username("admin").build();
    }

    @Test
    @DisplayName("Debería crear un AcademicPeriodConfig correctamente usando el patrón Builder")
    void shouldCreateAcademicPeriodConfigUsingBuilder_happyPath() {
        // Arrange
        String expectedId = "config-123";
        String expectedName = "Cambios de Grupo 2024-1";

        // Act
        AcademicPeriodConfig config = AcademicPeriodConfig.builder()
                .id(expectedId)
                .periodName(expectedName)
                .description("Período para cambios de grupo")
                .startDate(oneHourAgo)
                .endDate(oneHourLater)
                .active(true)
                .faculty(mockFaculty)
                .allowedRequestTypes("CHANGE_GROUP")
                .createdBy(mockUser)
                .createdAt(now)
                .build();

        // Assert
        assertThat(config).isNotNull();
        assertThat(config.getId()).isEqualTo(expectedId);
        assertThat(config.getPeriodName()).isEqualTo(expectedName);
        assertThat(config.getStartDate()).isEqualTo(oneHourAgo);
        assertThat(config.getEndDate()).isEqualTo(oneHourLater);
        assertThat(config.isActive()).isTrue();
        assertThat(config.getFaculty()).isEqualTo(mockFaculty);
        assertThat(config.getAllowedRequestTypes()).isEqualTo("CHANGE_GROUP");
        assertThat(config.getCreatedBy()).isEqualTo(mockUser);
    }

    @Test
    @DisplayName("Debería crear un AcademicPeriodConfig con valores por defecto usando el constructor vacío")
    void shouldCreateAcademicPeriodConfigWithDefaultValues_happyPath() {
        // Act
        AcademicPeriodConfig config = new AcademicPeriodConfig();

        // Assert
        assertThat(config).isNotNull();
        assertThat(config.getId()).isNull();
        assertThat(config.getPeriodName()).isNull();
        assertThat(config.isActive()).isFalse();
        assertThat(config.getFaculty()).isNull();
    }

    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode() para objetos iguales")
    void shouldVerifyEqualsAndHashCodeContract_happyPath() {
        // Arrange
        AcademicPeriodConfig config1 = AcademicPeriodConfig.builder()
                .id("config-xyz")
                .periodName("Periodo X")
                .build();

        AcademicPeriodConfig config2 = AcademicPeriodConfig.builder()
                .id("config-xyz")
                .periodName("Periodo X")
                .build();

        AcademicPeriodConfig config3 = AcademicPeriodConfig.builder()
                .id("config-abc")
                .build();

        // Act & Assert
        assertThat(config1).isEqualTo(config2);
        assertThat(config1.hashCode()).isEqualTo(config2.hashCode());
        assertThat(config1).isNotEqualTo(config3);
    }

    // --- Pruebas para el método isCurrentlyActive() ---

    @Test
    @DisplayName("isCurrentlyActive - Happy Path: debería ser verdadero si el período está activo y la fecha actual está dentro del rango")
    void isCurrentlyActive_happyPath_shouldReturnTrue_whenDateIsWithinRangeAndActive() {
        // Arrange
        AcademicPeriodConfig config = AcademicPeriodConfig.builder()
                .active(true)
                .startDate(oneHourAgo)
                .endDate(oneHourLater)
                .build();

        // Act
        boolean result = config.isCurrentlyActive();

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isCurrentlyActive - Sad Path: debería ser falso si el período está marcado como inactivo")
    void isCurrentlyActive_sadPath_shouldReturnFalse_whenActiveIsFalse() {
        // Arrange
        AcademicPeriodConfig config = AcademicPeriodConfig.builder()
                .active(false) // <-- Inactivo
                .startDate(oneHourAgo)
                .endDate(oneHourLater)
                .build();

        // Act
        boolean result = config.isCurrentlyActive();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isCurrentlyActive - Sad Path: debería ser falso si la fecha de inicio es nula")
    void isCurrentlyActive_sadPath_shouldReturnFalse_whenStartDateIsNull() {
        // Arrange
        AcademicPeriodConfig config = AcademicPeriodConfig.builder()
                .active(true)
                .startDate(null) // <-- Nulo
                .endDate(oneHourLater)
                .build();

        // Act
        boolean result = config.isCurrentlyActive();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isCurrentlyActive - Sad Path: debería ser falso si la fecha de fin es nula")
    void isCurrentlyActive_sadPath_shouldReturnFalse_whenEndDateIsNull() {
        // Arrange
        AcademicPeriodConfig config = AcademicPeriodConfig.builder()
                .active(true)
                .startDate(oneHourAgo)
                .endDate(null) // <-- Nulo
                .build();

        // Act
        boolean result = config.isCurrentlyActive();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isCurrentlyActive - Sad Path: debería ser falso si el período aún no ha comenzado")
    void isCurrentlyActive_sadPath_shouldReturnFalse_whenPeriodHasNotStarted() {
        // Arrange
        AcademicPeriodConfig config = AcademicPeriodConfig.builder()
                .active(true)
                .startDate(oneHourLater) // <-- En el futuro
                .endDate(oneHourLater.plusHours(1))
                .build();

        // Act
        boolean result = config.isCurrentlyActive();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isCurrentlyActive - Sad Path: debería ser falso si el período ya ha finalizado")
    void isCurrentlyActive_sadPath_shouldReturnFalse_whenPeriodHasEnded() {
        // Arrange
        AcademicPeriodConfig config = AcademicPeriodConfig.builder()
                .active(true)
                .startDate(oneHourAgo.minusHours(1))
                .endDate(oneHourAgo) // <-- En el pasado
                .build();

        // Act
        boolean result = config.isCurrentlyActive();

        // Assert
        assertThat(result).isFalse();
    }
}