package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para la clase Report")
class ReportTest {

    private Report report;
    @Mock
    private Faculty faculty;
    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        report = Report.builder()
                .id("rep-123")
                .reportType("APPROVAL_RATES")
                .title("Tasa de Aprobación del Período")
                .faculty(faculty)
                .generatedBy(user)
                .totalRequests(100)
                .approvedRequests(80)
                .rejectedRequests(20)
                .build();
    }

    @Test
    @DisplayName("calculateRates - Happy Path: debería calcular las tasas de aprobación y rechazo correctamente")
    void calculateRates_happyPath_shouldCalculateRates() {
        // Act
        report.calculateRates();
        // Assert
        assertThat(report.getApprovalRate()).isEqualTo(80.0);
        assertThat(report.getRejectionRate()).isEqualTo(20.0);
    }

    @Test
    @DisplayName("calculateRates - Sad Path: no debería hacer nada si totalRequests es cero")
    void calculateRates_sadPath_shouldDoNothing_whenTotalRequestsIsZero() {
        // Arrange
        report.setTotalRequests(0);
        // Act
        report.calculateRates();
        // Assert
        assertThat(report.getApprovalRate()).isNull();
        assertThat(report.getRejectionRate()).isNull();
    }

    @Test
    @DisplayName("calculateRates - Sad Path: no debería hacer nada si totalRequests es nulo")
    void calculateRates_sadPath_shouldDoNothing_whenTotalRequestsIsNull() {
        // Arrange
        report.setTotalRequests(null);
        // Act
        report.calculateRates();
        // Assert
        assertThat(report.getApprovalRate()).isNull();
        assertThat(report.getRejectionRate()).isNull();
    }

    @Test
    @DisplayName("calculateRates - Happy Path: debería manejar contadores de solicitudes aprobadas/rechazadas nulas")
    void calculateRates_happyPath_shouldHandleNullCounts_whenApprovedOrRejectedAreNull() {
        // Arrange
        report.setApprovedRequests(null);
        report.setRejectedRequests(null);
        // Act
        report.calculateRates();
        // Assert
        assertThat(report.getApprovalRate()).isEqualTo(0.0);
        assertThat(report.getRejectionRate()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Debería verificar el contrato equals() y hashCode()")
    void shouldVerifyEqualsAndHashCodeContract() {
        // Arrange
        Report report1 = Report.builder().id("rep-xyz").build();
        Report report2 = Report.builder().id("rep-xyz").build();
        Report report3 = Report.builder().id("rep-abc").build();

        // Act & Assert
        assertThat(report1).isEqualTo(report2);
        assertThat(report1.hashCode()).isEqualTo(report2.hashCode());
        assertThat(report1).isNotEqualTo(report3);
    }
}