package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para la clase Request")
class RequestTest {

    private Request request;
    @Mock
    private Student student;
    @Mock
    private Subject subject;
    @Mock
    private Group group;
    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        request = Request.builder()
                .id("req-123")
                .student(student)
                .type(RequestType.CHANGE_GROUP)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();
    }

    @Test
    @DisplayName("calculatePriority - Happy Path: debería calcular la puntuación de prioridad basada en la antigüedad")
    void calculatePriority_happyPath_shouldSetPriorityScoreBasedOnAge() {
        // Act
        request.calculatePriority();
        // Assert
        assertThat(request.getPriorityScore()).isGreaterThanOrEqualTo(2); // Creado hace 2 días
    }

    @Test
    @DisplayName("calculatePriority - Sad Path: no debería hacer nada si createdAt es nulo")
    void calculatePriority_sadPath_shouldDoNothing_whenCreatedAtIsNull() {
        // Arrange
        request.setCreatedAt(null);
        // Act
        request.calculatePriority();
        // Assert
        assertThat(request.getPriorityScore()).isNull();
    }

    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode()")
    void shouldVerifyEqualsAndHashCodeContract() {
        // Arrange
        Request req1 = Request.builder().id("req-xyz").build();
        Request req2 = Request.builder().id("req-xyz").build();
        Request req3 = Request.builder().id("req-abc").build();

        // Act & Assert
        assertThat(req1).isEqualTo(req2);
        assertThat(req1.hashCode()).isEqualTo(req2.hashCode());
        assertThat(req1).isNotEqualTo(req3);
    }
}