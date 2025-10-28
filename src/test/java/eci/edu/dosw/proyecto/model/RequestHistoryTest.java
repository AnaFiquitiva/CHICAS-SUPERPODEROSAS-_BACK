package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para la clase RequestHistory")
class RequestHistoryTest {

    @Mock
    private Request request;
    @Mock
    private User user;

    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode()")
    void shouldVerifyEqualsAndHashCodeContract() {
        // Arrange
        RequestHistory rh1 = RequestHistory.builder().id("rh-1").request(request).changedBy(user).build();
        RequestHistory rh2 = RequestHistory.builder().id("rh-1").request(request).changedBy(user).build();
        RequestHistory rh3 = RequestHistory.builder().id("rh-2").build();

        // Act & Assert
        assertThat(rh1).isEqualTo(rh2);
        assertThat(rh1.hashCode()).isEqualTo(rh2.hashCode());
        assertThat(rh1).isNotEqualTo(rh3);
    }
}