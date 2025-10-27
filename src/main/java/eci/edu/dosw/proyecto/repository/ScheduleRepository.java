package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.model.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {
    List<Schedule> findByGroupAndGroupActiveTrue(Group group);

    @Query("{ 'group.id': ?0 }")
    List<Schedule> findByGroupId(String groupId);

    @Query("{ 'group.subject.faculty.id': ?0 }")
    List<Schedule> findByFacultyId(String facultyId);

    @Query("{ 'dayOfWeek': ?0, 'startTime': ?1, 'endTime': ?2, 'group.active': true }")
    List<Schedule> findByDayAndTimeAndActiveGroup(String dayOfWeek, String startTime, String endTime);

    @Query("{ 'group.professor.id': ?0, 'group.active': true }")
    List<Schedule> findByProfessorIdAndActiveGroup(String professorId);
}
