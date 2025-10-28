package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para el Enum NotificationType")
class NotificationTypeTest {

    @Test
    @DisplayName("Debería contener todos los valores esperados del enum")
    void shouldContainAllExpectedValues() {
        // Act & Assert
        assertThat(NotificationType.values()).containsExactly(
                NotificationType.REQUEST_STATUS_CHANGE,
                NotificationType.ALERT_TRIGGERED,
                NotificationType.INFO_MESSAGE,
                NotificationType.WARNING_MESSAGE,
                NotificationType.SUCCESS_MESSAGE,
                NotificationType.DEADLINE_REMINDER,
                NotificationType.SPECIAL_APPROVAL
        );
    }
}