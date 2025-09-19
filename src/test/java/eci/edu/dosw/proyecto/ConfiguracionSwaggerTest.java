package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.configuracion.*;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfiguracionSwaggerTest {

    @Test
    public void testConfiguracionOpenAPI() {
        ConfiguracionSwagger config = new ConfiguracionSwagger();
        OpenAPI openAPI = config.configuracionOpenAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("Sistema de Gestión de Solicitudes de Cambio de Horario",
                openAPI.getInfo().getTitle());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertNotNull(openAPI.getServers());
        assertEquals(2, openAPI.getServers().size());
    }
}
