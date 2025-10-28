package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GroupMapperTest {

    @Autowired
    private GroupMapper groupMapper;

    private Group group; // Objeto de prueba principal
    private Subject subject;
    private Professor professor;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        // Creamos datos de prueba reutilizables
        subject = new Subject();
        subject.setId(String.valueOf(1L));
        subject.setName("Cálculo Diferencial");
        subject.setCode("MAT101");

        professor = new Professor();
        professor.setId(String.valueOf(10L));
        professor.setFirstName("Juan");
        professor.setLastName("Pérez");

        schedule = new Schedule();
        schedule.setId(String.valueOf(100L));
        schedule.setDayOfWeek(String.valueOf(DayOfWeek.MONDAY));
        schedule.setStartTime(String.valueOf(LocalTime.of(7, 0)));
        schedule.setEndTime(String.valueOf(LocalTime.of(9, 0)));
        schedule.setClassroom("A-201");

        group = createGroup(50L, "MAT101-01", 30, 25); // Usamos un método auxiliar
    }

    // --- Método auxiliar para crear grupos completos y evitar repetición ---
    private Group createGroup(Long id, String groupCode, int maxCapacity, int currentEnrollment) {
        Group newGroup = new Group();
        newGroup.setId(String.valueOf(id));
        newGroup.setGroupCode(groupCode);
        newGroup.setMaxCapacity(maxCapacity);
        newGroup.setCurrentEnrollment(currentEnrollment);
        newGroup.setSubject(subject);
        newGroup.setProfessor(professor);
        newGroup.setSchedules(List.of(schedule));
        return newGroup;
    }

    @Test
    @DisplayName("Camino Feliz: Debería mapear Group a GroupResponse correctamente")
    void toGroupResponse_shouldMapCorrectly() {
        // Act
        GroupResponse response = groupMapper.toGroupResponse(group);

        // Assert
        assertNotNull(response);
        assertEquals(group.getId(), response.getId());
        assertEquals(group.getGroupCode(), response.getGroupCode());
        assertEquals(group.getMaxCapacity(), response.getMaxCapacity());
        assertEquals(group.getCurrentEnrollment(), response.getCurrentEnrollment());

        // Verificar mapeo de objetos anidados
        assertNotNull(response.getSubject());
        assertEquals(subject.getName(), response.getSubject().getName());
        assertEquals(subject.getCode(), response.getSubject().getCode());

        assertNotNull(response.getProfessor());
        assertEquals(professor.getFirstName(), response.getProfessor().getFirstName());

        assertNotNull(response.getSchedules());
        assertEquals(1, response.getSchedules().size());
        assertEquals(schedule.getDayOfWeek(), response.getSchedules().get(0).getDayOfWeek());

        // Verificar mapeo con expresiones (lógica personalizada)
        double expectedOccupancy = (double) group.getCurrentEnrollment() / group.getMaxCapacity() * 100;
        assertEquals(expectedOccupancy, response.getOccupancyPercentage());
        assertTrue(response.isHasAvailableSpots());
    }

    @Test
    @DisplayName("Caso de Lista: Debería mapear una lista de Group a GroupResponseList")
    void toGroupResponseList_shouldMapListCorrectly() {
        // Arrange
        // ✅ CORRECCIÓN: Usamos el método auxiliar para crear un grupo completo.
        Group group2 = createGroup(51L, "FIS101-02", 40, 40); // Este grupo está lleno
        List<Group> groups = List.of(group, group2);

        // Act
        List<GroupResponse> responses = groupMapper.toGroupResponseList(groups);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(group.getGroupCode(), responses.get(0).getGroupCode());
        assertEquals(group2.getGroupCode(), responses.get(1).getGroupCode());

        // Verificamos la lógica para el segundo grupo
        assertEquals(100.0, responses.get(1).getOccupancyPercentage());
        assertFalse(responses.get(1).isHasAvailableSpots()); // No debería tener cupos
    }

    // ... Los demás tests de la clase permanecen igual ...
    @Test
    @DisplayName("Camino Feliz: Debería mapear Group a GroupBasicResponse correctamente")
    void toGroupBasicResponse_shouldMapCorrectly() {
        // Act
        GroupBasicResponse response = groupMapper.toGroupBasicResponse(group);

        // Assert
        assertNotNull(response);
        assertEquals(group.getGroupCode(), response.getGroupCode());
        assertEquals(subject.getName(), response.getSubjectName());
        assertEquals(subject.getCode(), response.getSubjectCode());

        double expectedOccupancy = (double) group.getCurrentEnrollment() / group.getMaxCapacity() * 100;
        assertEquals(expectedOccupancy, response.getOccupancyPercentage());
        assertTrue(response.isHasAvailableSpots());
    }
    

    @Test
    @DisplayName("Caso Límite: Debería devolver null si el input es null")
    void toGroupResponse_shouldReturnNull_whenInputIsNull() {
        // Act
        GroupResponse response = groupMapper.toGroupResponse(null);

        // Assert
        assertNull(response);
    }
}