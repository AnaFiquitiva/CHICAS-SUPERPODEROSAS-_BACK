package eci.edu.dosw.proyecto.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApprovalStatsResponseTest {

    @Test
    @DisplayName("Debería inicializarse con valores nulos por defecto")
    void deberiaInicializarConValoresNulos() {
        // Act: Creamos una instancia usando el constructor por defecto
        ApprovalStatsResponse response = new ApprovalStatsResponse();

        // Assert: Verificamos que todos los campos son nulos inicialmente
        assertNull(response.getFacultyId(), "facultyId debería ser nulo al inicio");
        assertNull(response.getFacultyName(), "facultyName debería ser nulo al inicio");
        assertNull(response.getTotalRequests(), "totalRequests debería ser nulo al inicio");
        assertNull(response.getApprovedRequests(), "approvedRequests debería ser nulo al inicio");
        assertNull(response.getRejectedRequests(), "rejectedRequests debería ser nulo al inicio");
        assertNull(response.getPendingRequests(), "pendingRequests debería ser nulo al inicio");
        assertNull(response.getApprovalRate(), "approvalRate debería ser nulo al inicio");
        assertNull(response.getRejectionRate(), "rejectionRate debería ser nulo al inicio");
        assertNull(response.getCalculatedAt(), "calculatedAt debería ser nulo al inicio");
    }

    @Test
    @DisplayName("Debería setear y obtener los valores correctamente usando los setters de Lombok")
    void deberiaSetearYObtenerValoresCorrectamente() {
        // Arrange
        ApprovalStatsResponse response = new ApprovalStatsResponse();
        LocalDateTime now = LocalDateTime.now();

        // Act: Usamos los setters generados por Lombok para poblar el objeto
        response.setFacultyId("FISI");
        response.setFacultyName("Facultad de Ingeniería de Sistemas");
        response.setTotalRequests(200);
        response.setApprovedRequests(150);
        response.setRejectedRequests(30);
        response.setPendingRequests(20);
        response.setApprovalRate(75.0);
        response.setRejectionRate(15.0);
        response.setCalculatedAt(now);

        // Assert: Verificamos que cada getter devuelva el valor que asignamos
        assertEquals("FISI", response.getFacultyId());
        assertEquals("Facultad de Ingeniería de Sistemas", response.getFacultyName());
        assertEquals(200, response.getTotalRequests());
        assertEquals(150, response.getApprovedRequests());
        assertEquals(30, response.getRejectedRequests());
        assertEquals(20, response.getPendingRequests());
        assertEquals(75.0, response.getApprovalRate());
        assertEquals(15.0, response.getRejectionRate());
        assertEquals(now, response.getCalculatedAt());
    }

    @Test
    @DisplayName("setAdditionalInfoRequests no debería tener ningún efecto en el objeto")
    void setAdditionalInfoRequests_noDeberiaHacerNada() {
        // Arrange
        ApprovalStatsResponse response = new ApprovalStatsResponse();
        response.setFacultyId("FISI"); // Seteamos un valor para asegurarnos que no cambia

        // Act: Llamamos al método que está vacío
        response.setAdditionalInfoRequests(999);

        // Assert: Verificamos que el estado del objeto no ha cambiado en absoluto
        assertEquals("FISI", response.getFacultyId(), "El estado del objeto no debería haber cambiado");
        assertNull(response.getTotalRequests(), "El estado del objeto no debería haber cambiado");
        // ... se podrían verificar todos los demás campos para asegurar que ninguno cambió
    }

    @Test
    @DisplayName("setCancelledRequests no debería tener ningún efecto en el objeto")
    void setCancelledRequests_noDeberiaHacerNada() {
        // Arrange
        ApprovalStatsResponse response = new ApprovalStatsResponse();
        response.setTotalRequests(100); // Seteamos un valor

        // Act: Llamamos al método que está vacío
        response.setCancelledRequests(50);

        // Assert: Verificamos que el estado del objeto no ha cambiado
        assertEquals(100, response.getTotalRequests(), "El estado del objeto no debería haber cambiado");
        assertNull(response.getFacultyId(), "El estado del objeto no debería haber cambiado");
        // ... se podrían verificar todos los demás campos
    }
}