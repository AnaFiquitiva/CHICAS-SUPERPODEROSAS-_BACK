package eci.edu.dosw.proyecto.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import eci.edu.dosw.proyecto.model.*;
import java.util.List;

@Repository
public interface ProfesorRepository extends MongoRepository<Profesor, String> {
    List<Profesor> findByDepartamento(String departamento);
    List<Profesor> findByEspecialidad(String especialidad);

    @Query("{ 'gruposAsignados.materia.facultad': ?0 }")
    List<Profesor> findByFacultad(String facultad);
}
