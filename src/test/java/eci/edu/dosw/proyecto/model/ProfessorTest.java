package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para la clase Professor")
class ProfessorTest {
    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode()")
    void shouldVerifyEqualsAndHashCodeContract() {
        Professor p1 = Professor.builder().id("prof-1").code("P001").build();
        Professor p2 = Professor.builder().id("prof-1").code("P001").build();
        Professor p3 = Professor.builder().id("prof-2").build();

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
        assertThat(p1).isNotEqualTo(p3);
    }
}