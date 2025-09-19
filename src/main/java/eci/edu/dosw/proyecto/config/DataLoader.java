
package eci.edu.dosw.proyecto.config;

import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.MateriaRepository;
import eci.edu.dosw.proyecto.repository.EstudianteRepository;
import eci.edu.dosw.proyecto.repository.DecanaturaRepository;
import eci.edu.dosw.proyecto.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initUsuarioDatabase(UsuarioRepository usuarioRepository) {
        return args -> {
            Usuario estudiante = new Estudiante("u-001", "2023001", "Laura Torres", "laura@uni.edu", "estudiante", "Ingeniería", 5);
            usuarioRepository.save(estudiante);
        };
    }

    @Bean
    CommandLineRunner initMainDatabase(
            MateriaRepository materiaRepository,
            EstudianteRepository estudianteRepository,
            DecanaturaRepository decanaturaRepository
    ) {
        return args -> {
            // Materias
            Materia mat1 = new Materia("MAT101", "Matemáticas", 3, "Ingeniería");
            Materia mat2 = new Materia("FIS101", "Física", 4, "Ingeniería");
            materiaRepository.save(mat1);
            materiaRepository.save(mat2);

            // Estudiantes
            Estudiante est1 = new Estudiante("est-001", "1234567890", "Juan Perez",
                    "juan@mail.escuelaing.edu.co", "Ingeniería", "Ingeniería", 3);
            Estudiante est2 = new Estudiante("est-002", "0987654321", "Ana Gómez",
                    "ana@mail.escuelaing.edu.co", "Ingeniería", "Ingeniería", 2);
            estudianteRepository.save(est1);
            estudianteRepository.save(est2);

            // Decanatura
            Decanatura dec1 = new Decanatura("dec-001", "DEC001", "Carlos Perez",
                    "c.perez@uni.edu", "Ingeniería");
            decanaturaRepository.save(dec1);
        };
    }
}
