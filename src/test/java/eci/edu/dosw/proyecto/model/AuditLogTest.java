package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para la clase AuditLog")
class AuditLogTest {

    private LocalDateTime now;
    private User mockUser;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        mockUser = User.builder().id("user-dean").build();
    }

    @Test
    @DisplayName("Debería crear un AuditLog correctamente usando el patrón Builder")
    void shouldCreateAuditLogUsingBuilder_happyPath() {
        // Arrange
        String expectedId = "log-456";
        String expectedAction = "UPDATE";

        // Act
        AuditLog log = AuditLog.builder()
                .id(expectedId)
                .action(expectedAction)
                .entityType("Group")
                .entityId("group-101")
                .description("Se modificó la capacidad del grupo.")
                .oldValues("maxCapacity: 30")
                .newValues("maxCapacity: 35")
                .performedBy(mockUser)
                .performedAt(now)
                .ipAddress("192.168.1.100")
                .build();

        // Assert
        assertThat(log).isNotNull();
        assertThat(log.getId()).isEqualTo(expectedId);
        assertThat(log.getAction()).isEqualTo(expectedAction);
        assertThat(log.getEntityType()).isEqualTo("Group");
        assertThat(log.getPerformedBy()).isEqualTo(mockUser);
        assertThat(log.getIpAddress()).isEqualTo("192.168.1.100");
    }

    @Test
    @DisplayName("Debería crear un AuditLog con valores por defecto usando el constructor vacío")
    void shouldCreateAuditLogWithDefaultValues_happyPath() {
        // Act
        AuditLog log = new AuditLog();

        // Assert
        assertThat(log).isNotNull();
        assertThat(log.getId()).isNull();
        assertThat(log.getAction()).isNull();
        assertThat(log.getEntityId()).isNull();
        assertThat(log.getPerformedBy()).isNull();
    }

    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode() para objetos iguales")
    void shouldVerifyEqualsAndHashCodeContract_happyPath() {
        // Arrange
        AuditLog log1 = AuditLog.builder()
                .id("log-xyz")
                .action("CREATE")
                .entityType("Student")
                .build();

        AuditLog log2 = AuditLog.builder()
                .id("log-xyz")
                .action("CREATE")
                .entityType("Student")
                .build();

        AuditLog log3 = AuditLog.builder()
                .id("log-abc")
                .build();

        // Act & Assert
        assertThat(log1).isEqualTo(log2);
        assertThat(log1.hashCode()).isEqualTo(log2.hashCode());
        assertThat(log1).isNotEqualTo(log3);
    }
}