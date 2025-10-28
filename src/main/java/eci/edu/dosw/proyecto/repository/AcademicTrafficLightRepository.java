package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.AcademicTrafficLight;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicTrafficLightRepository extends MongoRepository<AcademicTrafficLight, String> {
    Optional<AcademicTrafficLight> findByColor(String color);

    @Query("{ 'minimumGpa': { $lte: ?0 }, 'maximumGpa': { $gte: ?0 } }")
    Optional<AcademicTrafficLight> findByGpaRange(Double gpa);

    List<AcademicTrafficLight> findAllByOrderByMinimumGpaAsc();
}
