package eci.edu.dosw.proyecto.utils.mappers;

import eci.edu.dosw.proyecto.dto.ApprovalStatsResponse;
import eci.edu.dosw.proyecto.dto.GroupDemandResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReportMapperTest {

    @Autowired
    private ReportMapper reportMapper;

    @Test
    @DisplayName("Debería mapear Object[] a ApprovalStatsResponse correctamente")
    void toApprovalStatsResponse_shouldMapCorrectly() {
        // Arrange: Simula el resultado de una consulta SQL nativa
        Object[] statsData = {"FISI", 100L, 80L, 20L}; // facultyId, total, approved, rejected

        // Act
        ApprovalStatsResponse response = reportMapper.toApprovalStatsResponse(statsData);

        // Assert
        assertNotNull(response);
        assertEquals(80.0, response.getApprovalRate());
        assertEquals(20.0, response.getRejectionRate());
    }

    @Test
    @DisplayName("Debería devolver tasas en 0.0 si los datos son nulos o insuficientes")
    void toApprovalStatsResponse_shouldReturnZeroRates_whenDataIsInvalid() {
        // Arrange
        Object[] nullData = null;
        Object[] insufficientData = {"FISI", 100L}; // Faltan approved y rejected

        // Act & Assert para datos nulos
        ApprovalStatsResponse responseFromNull = reportMapper.toApprovalStatsResponse(nullData);
        assertEquals(0.0, responseFromNull.getApprovalRate());
        assertEquals(0.0, responseFromNull.getRejectionRate());

        // Act & Assert para datos insuficientes
        ApprovalStatsResponse responseFromInsufficient = reportMapper.toApprovalStatsResponse(insufficientData);
        assertEquals(0.0, responseFromInsufficient.getApprovalRate());
        assertEquals(0.0, responseFromInsufficient.getRejectionRate());
    }

    @Test
    @DisplayName("Debería mapear Object[] a GroupDemandResponse correctamente")
    void toGroupDemandResponse_shouldMapCorrectly() {
        // Arrange
        Object[] demandData = {"MAT101-01", "Cálculo Diferencial"};

        // Act
        GroupDemandResponse response = reportMapper.toGroupDemandResponse(demandData);

        // Assert
        assertNotNull(response);
        assertEquals("MAT101-01", response.getGroupCode());
        assertEquals("Cálculo Diferencial", response.getSubjectName());
    }
}