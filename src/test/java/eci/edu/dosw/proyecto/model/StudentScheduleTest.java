package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para la clase StudentSchedule")
class StudentScheduleTest {

    private StudentSchedule studentSchedule;
    @Mock
    private Student student;
    @Mock
    private Group group;
    @Mock
    private Schedule schedule; // Mock adicional para la prueba

    @BeforeEach
    void setUp() {
        studentSchedule = new StudentSchedule();
        studentSchedule.setStudent(student);
    }

    @Test
    @DisplayName("getSchedules - Happy Path: debería devolver una lista de horarios si hay grupos inscritos")
    void getSchedules_happyPath_shouldReturnListOfSchedules_whenEnrolledGroupsExist() {
        // --- CAMBIO 2: Configurar el comportamiento del mock ---
        // Evita una segunda NullPointerException dentro del método getSchedules() del modelo.
        when(group.getSchedules()).thenReturn(List.of(schedule));

        // Arrange
        studentSchedule.setEnrolledGroups(List.of(group)); // Esta línea ahora funcionará
        // Act
        List<Schedule> result = studentSchedule.getSchedules();
        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).contains(schedule);
    }

    @Test
    @DisplayName("getSchedules - Sad Path: debería devolver una lista vacía si la lista de grupos inscritos es nula")
    void getSchedules_sadPath_shouldReturnEmptyList_whenEnrolledGroupsIsNull() {
        // Arrange
        studentSchedule.setEnrolledGroups(null);
        // Act
        List<Schedule> result = studentSchedule.getSchedules();
        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getSchedules - Sad Path: debería devolver una lista vacía si no hay grupos inscritos")
    void getSchedules_sadPath_shouldReturnEmptyList_whenEnrolledGroupsIsEmpty() {
        // Arrange
        studentSchedule.setEnrolledGroups(List.of());
        // Act
        List<Schedule> result = studentSchedule.getSchedules();
        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode()")
    void shouldVerifyEqualsAndHashCodeContract() {
        // Arrange
        StudentSchedule ss1 = StudentSchedule.builder().id("ss-1").build();
        StudentSchedule ss2 = StudentSchedule.builder().id("ss-1").build();
        StudentSchedule ss3 = StudentSchedule.builder().id("ss-2").build();

        // Act & Assert
        assertThat(ss1).isEqualTo(ss2);
        assertThat(ss1.hashCode()).isEqualTo(ss2.hashCode());
        assertThat(ss1).isNotEqualTo(ss3);
    }
}