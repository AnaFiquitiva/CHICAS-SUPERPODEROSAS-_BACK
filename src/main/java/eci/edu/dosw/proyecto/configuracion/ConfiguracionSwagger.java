package eci.edu.dosw.proyecto.configuracion;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger/OpenAPI para la documentación de la API
 */
@Configuration
public class ConfiguracionSwagger {

    @Bean
    public OpenAPI configuracionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Gestión de Solicitudes de Cambio de Horario")
                        .version("1.0.0")
                        .description("""
                            API RESTful para el sistema de gestión de solicitudes de cambio de horario universitario.
                            
                            ## Funcionalidades principales:
                            - Creación de solicitudes por parte de estudiantes
                            - Aprobación/rechazo de solicitudes por parte de decanos
                            - Consulta de solicitudes por estado, estudiante o ID
                            - Gestión completa del ciclo de vida de las solicitudes
                            
                            ## Roles del sistema:
                            - **ESTUDIANTE**: Puede crear y consultar sus propias solicitudes
                            - **DECANATURA**: Puede aprobar, rechazar y consultar todas las solicitudes
                            - **ADMINISTRADOR**: Acceso completo al sistema
                            """)
                        .contact(new Contact()
                                .name("Equipo de Desarrollo - Universidad ECI")
                                .email("desarrollo@eci.edu.co")
                                .url("https://www.escuelaing.edu.co"))
                        .license(new License()
                                .name("Licencia Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Servidor de desarrollo local"))
                .addServersItem(new Server()
                        .url("https://api.uni.edu.co")
                        .description("Servidor de producción"));
    }
}