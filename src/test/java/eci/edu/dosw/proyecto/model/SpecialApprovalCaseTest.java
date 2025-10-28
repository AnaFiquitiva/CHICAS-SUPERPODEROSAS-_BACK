package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para la clase SpecialApprovalCase")
class SpecialApprovalCaseTest {

    @Mock
    private Request request;
    @Mock
    private User user;

    @Test
    @DisplayName("getOverriddenConstraintsDescription - Happy Path: debería devolver una descripción formateada")
    void getOverriddenConstraintsDescription_happyPath_shouldReturnFormattedDescription() {
        // Arrange
        SpecialApprovalCase approvalCase = SpecialApprovalCase.builder()
                .request(request)
                .approvedBy(user)
                .constraintsOverridden("CUPO, PREREQUISITOS")
                .build();
        // Act
        String description = approvalCase.getOverriddenConstraintsDescription();
        // Assert
        assertThat(description).isEqualTo("Aprobación especial que sobrepasó: CUPO, PREREQUISITOS");
    }

    @Test
    @DisplayName("getOverriddenConstraintsDescription - Sad Path: debería manejar restricciones nulas")
    void getOverriddenConstraintsDescription_sadPath_shouldHandleNullConstraints() {
        // Arrange
        SpecialApprovalCase approvalCase = SpecialApprovalCase.builder()
                .constraintsOverridden(null)
                .build();
        // Act
        String description = approvalCase.getOverriddenConstraintsDescription();
        // Assert
        assertThat(description).isEqualTo("Aprobación especial que sobrepasó: null");
    }

    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode()")
    void shouldVerifyEqualsAndHashCodeContract() {
        // Arrange
        SpecialApprovalCase sac1 = SpecialApprovalCase.builder().id("sac-1").build();
        SpecialApprovalCase sac2 = SpecialApprovalCase.builder().id("sac-1").build();
        SpecialApprovalCase sac3 = SpecialApprovalCase.builder().id("sac-2").build();

        // Act & Assert
        assertThat(sac1).isEqualTo(sac2);
        assertThat(sac1.hashCode()).isEqualTo(sac2.hashCode());
        assertThat(sac1).isNotEqualTo(sac3);
    }
}