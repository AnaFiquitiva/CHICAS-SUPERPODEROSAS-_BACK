package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {

    List<Schedule> findByStudent_StudentCode(String studentCode);

    List<Schedule> findByStudent_StudentCodeAndSemester_IsCurrent(String studentCode, boolean isCurrent);
}
