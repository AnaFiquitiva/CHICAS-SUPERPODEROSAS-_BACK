package eci.edu.dosw.proyecto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import eci.edu.dosw.proyecto.repository.MateriaRepository;
import eci.edu.dosw.proyecto.model.Materia;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MateriaRepositoryTest {

    @Autowired
    private MateriaRepository materiaRepository;

    @Test
    void testGuardarYLeerMateria() {
        Materia materia = new Materia("idTest", "nombreTest", 3, "Ingeniería");
        materiaRepository.save(materia);

        Materia encontrada = materiaRepository.findById("idTest").orElse(null);
        assertThat(encontrada).isNotNull();
        assertThat(encontrada.getNombre()).isEqualTo("nombreTest");

        materiaRepository.deleteById("idTest");
    }
}

