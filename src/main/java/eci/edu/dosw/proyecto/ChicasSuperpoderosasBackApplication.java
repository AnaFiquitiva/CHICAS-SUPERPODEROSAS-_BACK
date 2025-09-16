package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.model.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChicasSuperpoderosasBackApplication {
    
    public static void main(String[] args) {
        // Crear materias
        Materia calculo = new Materia("MAT001", "Cálculo I", 4, Facultades.INGENIERIA);
        Materia fisica = new Materia("FIS001", "Física I", 3, Facultades.INGENIERIA);

        // Crear grupos
        Grupo calculoG1 = new Grupo("G1", calculo, "Prof. García", "L-M-V 8:00-10:00", 30);
        Grupo calculoG2 = new Grupo("G2", calculo, "Prof. López", "M-J-V 10:00-12:00", 25);

        calculo.agregarGrupo(calculoG1);
        calculo.agregarGrupo(calculoG2);

        // Crear usuarios usando Factory
        Estudiante estudiante = UsuarioFactory.crearEstudiante(
                "EST001", "123456", "Juan Pérez", "juan.perez@mail.escuelaing.edu.co",
                Facultades.INGENIERIA, "Ingeniería de Sistemas", 3
        );

        Decanatura decanatura = UsuarioFactory.crearDecanatura(
                "DEC001", "DEC001", "Decanatura Ingeniería", "decanatura.ing@escuelaing.edu.co",
                Facultades.INGENIERIA
        );

        // El estudiante crea una solicitud de cambio
        try {
            SolicitudCambio solicitud = estudiante.crearSolicitud(
                    calculo, calculoG1, calculo, calculoG2,
                    "Cruce de horarios con otra materia"
            );

            System.out.println("Solicitud creada: " + solicitud.getId());
            System.out.println("Estado inicial: " + solicitud.getEstadoString());
            System.out.println("Prioridad: " + solicitud.getPrioridad().calcularPrioridad());

            // Registrar la decanatura como observadora
            solicitud.adjuntar(decanatura);

            // La decanatura evalúa y aprueba la solicitud
            decanatura.evaluarSolicitud(solicitud);

            if (RolValidator.puedeGestionarSolicitudes(decanatura.getRol())) {
                decanatura.aprobarSolicitud(solicitud);
                System.out.println("Estado final: " + solicitud.getEstadoString());
            }

        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage());
        }

        // Ejemplo de validaciones
        System.out.println("\n=== Validaciones ===");
        System.out.println("¿Es rol válido ESTUDIANTE? " + RolValidator.esRolValido(Roles.ESTUDIANTE));
        System.out.println("¿Puede transicionar de PENDIENTE a APROBADO? " + 
                EstadoValidator.puedeTransicionarA(EstadosSolicitud.PENDIENTE, EstadosSolicitud.APROBADO));
        System.out.println("¿Es estado válido TEST? " + EstadoValidator.esEstadoValido("TEST"));
        
        // Iniciar Spring Boot
        SpringApplication.run(ChicasSuperpoderosasBackApplication.class, args);
    }
}