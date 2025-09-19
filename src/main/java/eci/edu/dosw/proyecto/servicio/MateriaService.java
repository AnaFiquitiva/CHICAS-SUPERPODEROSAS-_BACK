package eci.edu.dosw.proyecto.servicio;

import eci.edu.dosw.proyecto.model.Materia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import eci.edu.dosw.proyecto.repository.MateriaRepository;
import java.util.List;

@Service
public class MateriaService {
    @Autowired
    private MateriaRepository materiaRepository;

    public List<Materia> obtenerTodas() {
        return materiaRepository.findAll();
    }

    public Materia guardar(Materia materia) {
        return materiaRepository.save(materia);
    }
}
