package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para la clase Alert")
class AlertTest {

    private LocalDateTime now;
    private Group mockGroup;
    private User mockUser;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        mockGroup = Group.builder().id("group-101").build();
        mockUser = User.builder().id("user-admin").build();
    }

    @Test
    @DisplayName("Debería crear una Alerta correctamente usando el patrón Builder")
    void shouldCreateAlertUsingBuilder_happyPath() {
        // Arrange
        String expectedId = "alert-123";
        AlertType expectedType = AlertType.GROUP_CAPACITY_90;
        String expectedTitle = "Grupo casi lleno";

        // Act
        Alert alert = Alert.builder()
                .id(expectedId)
                .type(expectedType)
                .title(expectedTitle)
                .description("El grupo G01 de Cálculo ha alcanzado el 90% de su capacidad.")
                .severity("HIGH")
                .group(mockGroup)
                .resolved(false)
                .resolvedBy(mockUser)
                .resolvedAt(now)
                .createdAt(now.minusHours(1))
                .build();

        // Assert
        assertThat(alert).isNotNull();
        assertThat(alert.getId()).isEqualTo(expectedId);
        assertThat(alert.getType()).isEqualTo(expectedType);
        assertThat(alert.getTitle()).isEqualTo(expectedTitle);
        assertThat(alert.getGroup()).isEqualTo(mockGroup);
        assertThat(alert.isResolved()).isFalse();
        assertThat(alert.getResolvedBy()).isEqualTo(mockUser);
    }

    @Test
    @DisplayName("Debería crear una Alerta con valores por defecto usando el constructor vacío")
    void shouldCreateAlertWithDefaultValues_happyPath() {
        // Act
        Alert alert = new Alert();

        // Assert
        assertThat(alert).isNotNull();
        assertThat(alert.getId()).isNull();
        assertThat(alert.getType()).isNull();
        assertThat(alert.getTitle()).isNull();
        assertThat(alert.isResolved()).isFalse(); // El valor por defecto de boolean es false
        assertThat(alert.getGroup()).isNull();
    }

    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode() para objetos iguales")
    void shouldVerifyEqualsAndHashCodeContract_happyPath() {
        // Arrange
        Alert alert1 = Alert.builder()
                .id("alert-xyz")
                .type(AlertType.SYSTEM_ALERT)
                .title("Título X")
                .build();

        Alert alert2 = Alert.builder()
                .id("alert-xyz")
                .type(AlertType.SYSTEM_ALERT)
                .title("Título X")
                .build();

        Alert alert3 = Alert.builder()
                .id("alert-abc")
                .build();

        // Act & Assert
        assertThat(alert1).isEqualTo(alert2);
        assertThat(alert1.hashCode()).isEqualTo(alert2.hashCode());
        assertThat(alert1).isNotEqualTo(alert3);
    }
}