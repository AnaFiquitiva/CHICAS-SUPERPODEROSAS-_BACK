package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static eci.edu.dosw.proyecto.model.AssignmentStatus.PENDING;
import static eci.edu.dosw.proyecto.model.AssignmentType.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class) // ¡Crucial para que @Mock funcione!
@DisplayName("Pruebas unitarias para la clase ManualAssignment")
class ManualAssignmentTest {

    private ManualAssignment manualAssignment;
    @Mock
    private Student student;
    @Mock
    private Subject subject;
    @Mock
    private Group group;
    @Mock
    private User assignedBy;

    @BeforeEach
    void setUp() {
        manualAssignment = ManualAssignment.builder()
                .student(student)
                .subject(subject)
                .group(group)
                .type(GROUP_ASSIGNMENT)
                .status(PENDING)
                .assignedBy(assignedBy)
                .build();
    }

    @Test
    @DisplayName("isValidForExecution - Happy Path: debería ser verdadero para GROUP_ASSIGNMENT si todo es válido")
    void isValidForExecution_happyPath_shouldReturnTrue_forGroupAssignment() {
        // Act
        boolean result = manualAssignment.isValidForExecution();
        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isValidForExecution - Happy Path: debería ser verdadero para SUBJECT_ASSIGNMENT si todo es válido")
    void isValidForExecution_happyPath_shouldReturnTrue_forSubjectAssignment() {
        // Arrange
        manualAssignment.setType(SUBJECT_ASSIGNMENT);
        manualAssignment.setGroup(null); // No se necesita grupo para esta asignación
        // Act
        boolean result = manualAssignment.isValidForExecution();
        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isValidForExecution - Sad Path: debería ser falso si el estado no es PENDING")
    void isValidForExecution_sadPath_shouldReturnFalse_whenStatusIsNotPending() {
        // Arrange
        manualAssignment.setStatus(AssignmentStatus.EXECUTED);
        // Act
        boolean result = manualAssignment.isValidForExecution();
        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValidForExecution - Sad Path: debería ser falso si el estudiante es nulo")
    void isValidForExecution_sadPath_shouldReturnFalse_whenStudentIsNull() {
        // Arrange
        manualAssignment.setStudent(null);
        // Act
        boolean result = manualAssignment.isValidForExecution();
        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValidForExecution - Sad Path: debería ser falso si el usuario asignado es nulo")
    void isValidForExecution_sadPath_shouldReturnFalse_whenAssignedByIsNull() {
        // Arrange
        manualAssignment.setAssignedBy(null);
        // Act
        boolean result = manualAssignment.isValidForExecution();
        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValidForExecution - Sad Path: debería ser falso para GROUP_ASSIGNMENT si el grupo es nulo")
    void isValidForExecution_sadPath_shouldReturnFalse_whenGroupAssignmentTypeButGroupIsNull() {
        // Arrange
        manualAssignment.setType(GROUP_ASSIGNMENT);
        manualAssignment.setGroup(null);
        // Act
        boolean result = manualAssignment.isValidForExecution();
        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValidForExecution - Sad Path: debería ser falso para SUBJECT_ASSIGNMENT si la materia es nula")
    void isValidForExecution_sadPath_shouldReturnFalse_whenSubjectAssignmentTypeButSubjectIsNull() {
        // Arrange
        manualAssignment.setType(SUBJECT_ASSIGNMENT);
        manualAssignment.setSubject(null);
        // Act
        boolean result = manualAssignment.isValidForExecution();
        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode()")
    void shouldVerifyEqualsAndHashCodeContract() {
        // Arrange
        ManualAssignment ma1 = ManualAssignment.builder().id("ma-xyz").build();
        ManualAssignment ma2 = ManualAssignment.builder().id("ma-xyz").build();
        ManualAssignment ma3 = ManualAssignment.builder().id("ma-abc").build();

        // Act & Assert
        assertThat(ma1).isEqualTo(ma2);
        assertThat(ma1.hashCode()).isEqualTo(ma2.hashCode());
        assertThat(ma1).isNotEqualTo(ma3);
    }
}