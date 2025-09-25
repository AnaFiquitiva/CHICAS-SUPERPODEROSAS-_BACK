package eci.edu.dosw.proyecto.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import eci.edu.dosw.proyecto.model.*;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface HorarioRepository extends MongoRepository<Horario, String> {
    List<Horario> findByDia(DiaSemana dia);

    @Query("{ 'dia': ?0, 'horaInicio': { $lte: ?2 }, 'horaFin': { $gte: ?1 } }")
    List<Horario> findConCruce(DiaSemana dia, LocalTime inicio, LocalTime fin);

    @Query("{ 'aula': ?0, 'dia': ?1, 'horaInicio': { $lt: ?3 }, 'horaFin': { $gt: ?2 } }")
    List<Horario> findAulaOcupada(String aula, DiaSemana dia, LocalTime inicio, LocalTime fin);
}