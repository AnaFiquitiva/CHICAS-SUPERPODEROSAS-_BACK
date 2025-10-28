package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para el Enum ReportType")
class ReportTypeTest {

    @Test
    @DisplayName("Debería contener todos los valores esperados del enum")
    void shouldContainAllExpectedValues() {
        // Act & Assert
        assertThat(ReportType.values()).containsExactly(
                ReportType.MOST_REQUESTED_GROUPS,
                ReportType.APPROVAL_VS_REJECTION_RATES,
                ReportType.REASSIGNMENT_STATISTICS,
                ReportType.GROUP_OCCUPANCY,
                ReportType.FACULTY_PERFORMANCE,
                ReportType.STUDENT_PROGRESS,
                ReportType.EXCEPTIONAL_CASES
        );
    }
}