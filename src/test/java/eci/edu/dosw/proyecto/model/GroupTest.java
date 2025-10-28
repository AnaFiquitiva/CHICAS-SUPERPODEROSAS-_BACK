package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para la clase Group")
class GroupTest {

    private Group group;

    @Mock
    private Subject subject;

    @BeforeEach
    void setUp() {
        group = Group.builder()
                .id("grp-101")
                .groupCode("G01")
                .maxCapacity(10)
                .currentEnrollment(5)
                .subject(subject)
                // Inicializamos explícitamente los contadores para un estado predecible
                .totalRequests(0)
                .approvedRequests(0)
                .rejectedRequests(0)
                .pendingRequests(0)
                .build();
    }

    @Test
    @DisplayName("hasAvailableSpots - Happy Path: debería ser verdadero si hay cupos disponibles")
    void hasAvailableSpots_happyPath_shouldReturnTrue_whenEnrollmentIsLessThanCapacity() {
        // Act
        boolean result = group.hasAvailableSpots();
        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("hasAvailableSpots - Sad Path: debería ser falso si el grupo está lleno")
    void hasAvailableSpots_sadPath_shouldReturnFalse_whenGroupIsFull() {
        // Arrange
        group.setCurrentEnrollment(10);
        // Act
        boolean result = group.hasAvailableSpots();
        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("getOccupancyPercentage - Happy Path: debería calcular el porcentaje correcto")
    void getOccupancyPercentage_happyPath_shouldReturnCorrectPercentage() {
        // Arrange
        group.setCurrentEnrollment(5); // 5/10 = 50%
        // Act
        Double percentage = group.getOccupancyPercentage();
        // Assert
        assertThat(percentage).isEqualTo(50.0);
    }

    @Test
    @DisplayName("getOccupancyPercentage - Sad Path: debería devolver 0.0 si la capacidad máxima es cero o nula")
    void getOccupancyPercentage_sadPath_shouldReturnZero_whenMaxCapacityIsZero() {
        // Arrange
        group.setMaxCapacity(0);
        // Act
        Double percentage = group.getOccupancyPercentage();
        // Assert
        assertThat(percentage).isEqualTo(0.0);
    }

    @Test
    @DisplayName("shouldTrigger90PercentAlert - Happy Path: debería ser verdadero si la ocupación es del 90% o más")
    void shouldTrigger90PercentAlert_happyPath_shouldReturnTrue_whenOccupancyIs90PercentOrMore() {
        // Arrange
        group.setCurrentEnrollment(9); // 9/10 = 90%
        // Act
        boolean result = group.shouldTrigger90PercentAlert();
        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("shouldTrigger90PercentAlert - Sad Path: debería ser falso si la ocupación es menor al 90%")
    void shouldTrigger90PercentAlert_sadPath_shouldReturnFalse_whenOccupancyIsLessThan90Percent() {
        // Arrange
        group.setCurrentEnrollment(8); // 8/10 = 80%
        // Act
        boolean result = group.shouldTrigger90PercentAlert();
        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("incrementRequestCount - Happy Path: debería incrementar solicitudes aprobadas")
    void incrementRequestCount_happyPath_shouldIncrementApprovedRequests_whenApprovedIsTrue() {
        // Act
        group.incrementRequestCount(true);
        // Assert
        assertThat(group.getTotalRequests()).isEqualTo(1);
        assertThat(group.getApprovedRequests()).isEqualTo(1);
        assertThat(group.getRejectedRequests()).isEqualTo(0);
    }

    @Test
    @DisplayName("incrementRequestCount - Happy Path: debería incrementar solicitudes rechazadas")
    void incrementRequestCount_happyPath_shouldIncrementRejectedRequests_whenApprovedIsFalse() {
        // Act
        group.incrementRequestCount(false);
        // Assert
        assertThat(group.getTotalRequests()).isEqualTo(1);
        assertThat(group.getRejectedRequests()).isEqualTo(1);
        assertThat(group.getApprovedRequests()).isEqualTo(0);
    }

    @Test
    @DisplayName("incrementRequestCount - Sad Path: debería manejar contadores nulos correctamente")
    void incrementRequestCount_sadPath_shouldHandleNullCountsCorrectly() {
        // Arrange
        group.setTotalRequests(null);
        group.setApprovedRequests(null);
        // Act
        group.incrementRequestCount(true);
        // Assert
        assertThat(group.getTotalRequests()).isEqualTo(1);
        assertThat(group.getApprovedRequests()).isEqualTo(1);
    }

    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode()")
    void shouldVerifyEqualsAndHashCodeContract() {
        // Arrange
        Group group1 = Group.builder().id("grp-xyz").build();
        Group group2 = Group.builder().id("grp-xyz").build();
        Group group3 = Group.builder().id("grp-abc").build();

        // Act & Assert
        assertThat(group1).isEqualTo(group2);
        assertThat(group1.hashCode()).isEqualTo(group2.hashCode());
        assertThat(group1).isNotEqualTo(group3);
    }
}