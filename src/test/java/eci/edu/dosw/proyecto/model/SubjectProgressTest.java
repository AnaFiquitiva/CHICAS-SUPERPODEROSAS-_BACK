package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para la clase SubjectProgress")
class SubjectProgressTest {

    @Test
    @DisplayName("getTrafficLightColor - Happy Path: debería devolver el color correcto para cada estado")
    void getTrafficLightColor_happyPath_shouldReturnCorrectColor_forEachStatus() {
        // Arrange & Act & Assert
        SubjectProgress pending = new SubjectProgress(); pending.setStatus("PENDING");
        assertThat(pending.getTrafficLightColor()).isEqualTo("GRAY");

        SubjectProgress inProgress = new SubjectProgress(); inProgress.setStatus("IN_PROGRESS");
        assertThat(inProgress.getTrafficLightColor()).isEqualTo("YELLOW");

        SubjectProgress approved = new SubjectProgress(); approved.setStatus("APPROVED");
        assertThat(approved.getTrafficLightColor()).isEqualTo("GREEN");

        SubjectProgress failed = new SubjectProgress(); failed.setStatus("FAILED");
        assertThat(failed.getTrafficLightColor()).isEqualTo("RED");
    }

    @Test
    @DisplayName("getTrafficLightColor - Sad Path: debería devolver GRAY para un estado desconocido")
    void getTrafficLightColor_sadPath_shouldReturnGray_forUnknownStatus() {
        // Arrange
        SubjectProgress unknown = new SubjectProgress();
        unknown.setStatus("UNKNOWN_STATUS");
        // Act
        String color = unknown.getTrafficLightColor();
        // Assert
        assertThat(color).isEqualTo("GRAY");
    }

    @Test
    @DisplayName("canBeRetaken - Happy Path: debería ser verdadero si está reprobada con baja nota y pocos intentos")
    void canBeRetaken_happyPath_shouldReturnTrue_whenFailedWithLowGradeAndFewAttempts() {
        // Arrange
        SubjectProgress failed = new SubjectProgress();
        failed.setStatus("FAILED");
        failed.setGrade(2.5);
        failed.setAttempt(1);
        // Act
        boolean result = failed.canBeRetaken();
        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("canBeRetaken - Sad Path: debería ser falso si el estado no es FAILED")
    void canBeRetaken_sadPath_shouldReturnFalse_whenStatusIsNotFailed() {
        // Arrange
        SubjectProgress approved = new SubjectProgress();
        approved.setStatus("APPROVED");
        // Act
        boolean result = approved.canBeRetaken();
        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("canBeRetaken - Sad Path: debería ser falso si la nota es nula")
    void canBeRetaken_sadPath_shouldReturnFalse_whenGradeIsNull() {
        // Arrange
        SubjectProgress failed = new SubjectProgress();
        failed.setStatus("FAILED");
        failed.setGrade(null);
        // Act
        boolean result = failed.canBeRetaken();
        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("canBeRetaken - Sad Path: debería ser falso si la nota es demasiado alta (aprobatoria)")
    void canBeRetaken_sadPath_shouldReturnFalse_whenGradeIsTooHigh() {
        // Arrange
        SubjectProgress failed = new SubjectProgress();
        failed.setStatus("FAILED");
        failed.setGrade(3.5); // Nota para aprobar es 3.0
        // Act
        boolean result = failed.canBeRetaken();
        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("canBeRetaken - Sad Path: debería ser falso si alcanzó el máximo de intentos")
    void canBeRetaken_sadPath_shouldReturnFalse_whenMaxAttemptsReached() {
        // Arrange
        SubjectProgress failed = new SubjectProgress();
        failed.setStatus("FAILED");
        failed.setGrade(2.5);
        failed.setAttempt(3);
        // Act
        boolean result = failed.canBeRetaken();
        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode()")
    void shouldVerifyEqualsAndHashCodeContract() {
        // Arrange
        SubjectProgress sp1 = SubjectProgress.builder().id("sp-1").build();
        SubjectProgress sp2 = SubjectProgress.builder().id("sp-1").build();
        SubjectProgress sp3 = SubjectProgress.builder().id("sp-2").build();

        // Act & Assert
        assertThat(sp1).isEqualTo(sp2);
        assertThat(sp1.hashCode()).isEqualTo(sp2.hashCode());
        assertThat(sp1).isNotEqualTo(sp3);
    }
    @Test
    @DisplayName("canBeRetaken - Happy Path: debería ser verdadero si está reprobada y el número de intentos es nulo")
    void canBeRetaken_happyPath_shouldReturnTrue_whenFailedAndAttemptIsNull() {
        // Arrange
        SubjectProgress failed = new SubjectProgress();
        failed.setStatus("FAILED");
        failed.setGrade(2.5);
        failed.setAttempt(null); // <-- Esta es la clave
        // Act
        boolean result = failed.canBeRetaken();
        // Assert
        assertThat(result).isTrue();
    }
}