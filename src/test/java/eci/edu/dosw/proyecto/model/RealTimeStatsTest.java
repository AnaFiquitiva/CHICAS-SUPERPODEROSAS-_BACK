package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para la clase RealTimeStats")
class RealTimeStatsTest {

    private RealTimeStats stats;
    @Mock
    private Faculty faculty;

    @BeforeEach
    void setUp() {
        stats = new RealTimeStats();
        stats.setFaculty(faculty);
    }

    @Test
    @DisplayName("updateCalculationTime - Happy Path: debería actualizar la fecha de cálculo a la hora actual")
    void updateCalculationTime_happyPath_shouldUpdateCalculationTimeToNow() {
        // Act
        stats.updateCalculationTime();
        // Assert
        assertThat(stats.getCalculatedAt()).isNotNull();
        assertThat(stats.getCalculatedAt()).isAfter(LocalDateTime.now().minusSeconds(1));
    }

    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode()")
    void shouldVerifyEqualsAndHashCodeContract() {
        // Arrange
        RealTimeStats rts1 = RealTimeStats.builder().id("rts-1").build();
        RealTimeStats rts2 = RealTimeStats.builder().id("rts-1").build();
        RealTimeStats rts3 = RealTimeStats.builder().id("rts-2").build();

        // Act & Assert
        assertThat(rts1).isEqualTo(rts2);
        assertThat(rts1.hashCode()).isEqualTo(rts2.hashCode());
        assertThat(rts1).isNotEqualTo(rts3);
    }
}