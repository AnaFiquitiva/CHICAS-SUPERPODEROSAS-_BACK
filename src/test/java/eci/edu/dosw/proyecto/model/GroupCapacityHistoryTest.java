package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para la clase GroupCapacityHistory")
class GroupCapacityHistoryTest {

    @Mock
    private Group group;

    private GroupCapacityHistory history;

    @BeforeEach
    void setUp() {
        history = GroupCapacityHistory.builder()
                .group(group)
                .previousCapacity(10)
                .newCapacity(20)
                .build();
    }

    @Test
    @DisplayName("isValidCapacityChange - Happy Path: debería ser verdadero si la nueva capacidad es mayor que la inscripción actual")
    void isValidCapacityChange_happyPath_shouldReturnTrue_whenNewCapacityIsHigherThanCurrentEnrollment() {
        when(group.getCurrentEnrollment()).thenReturn(15);
        history.setNewCapacity(20);
        boolean result = history.isValidCapacityChange();
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isValidCapacityChange - Sad Path: debería ser falso si la nueva capacidad es nula")
    void isValidCapacityChange_sadPath_shouldReturnFalse_whenNewCapacityIsNull() {
        history.setNewCapacity(null);
        boolean result = history.isValidCapacityChange();
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValidCapacityChange - Sad Path: debería ser falso si la nueva capacidad es cero o negativa")
    void isValidCapacityChange_sadPath_shouldReturnFalse_whenNewCapacityIsZeroOrNegative() {
        history.setNewCapacity(0);
        boolean result = history.isValidCapacityChange();
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValidCapacityChange - Sad Path: debería ser falso si la nueva capacidad es menor que la inscripción actual")
    void isValidCapacityChange_sadPath_shouldReturnFalse_whenNewCapacityIsLowerThanCurrentEnrollment() {
        when(group.getCurrentEnrollment()).thenReturn(25);
        history.setNewCapacity(20);
        boolean result = history.isValidCapacityChange();
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValidCapacityChange - Happy Path: debería ser verdadero si previousCapacity es nulo pero la nueva capacidad es válida")
    void isValidCapacityChange_happyPath_shouldReturnTrue_whenPreviousCapacityIsNull() {
        // Arrange
        // La línea when(group.getCurrentEnrollment()).thenReturn(15); se eliminó porque no es necesaria.
        history.setPreviousCapacity(null);
        history.setNewCapacity(20);
        // Act
        boolean result = history.isValidCapacityChange();
        // Assert
        assertThat(result).isTrue();
    }
}