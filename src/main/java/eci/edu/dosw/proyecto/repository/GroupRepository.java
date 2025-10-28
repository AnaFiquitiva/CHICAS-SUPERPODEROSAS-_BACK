package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Faculty;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.model.Professor;
import eci.edu.dosw.proyecto.model.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {
    Optional<Group> findByGroupCodeAndSubjectAndActiveTrue(String groupCode, Subject subject);

    List<Group> findBySubjectAndActiveTrue(Subject subject);

    List<Group> findBySubjectFacultyAndActiveTrue(Faculty faculty);

    List<Group> findByProfessorAndActiveTrue(Professor professor);

    List<Group> findByActiveTrue();

    @Query("{ 'subject.id': ?0, 'active': true }")
    List<Group> findBySubjectIdAndActiveTrue(String subjectId);

    @Query("{ 'professor.id': ?0, 'active': true }")
    List<Group> findByProfessorIdAndActiveTrue(String professorId);

    @Query("{ 'subject.faculty.id': ?0, 'active': true }")
    List<Group> findByFacultyIdAndActiveTrue(String facultyId);

    @Query("{ 'currentEnrollment': { $gte: ?0 }, 'active': true }")
    List<Group> findByCurrentEnrollmentGreaterThanEqualAndActiveTrue(Integer minEnrollment);

    @Query("{ 'currentEnrollment': { $lt: ?0 }, 'active': true }")
    List<Group> findByCurrentEnrollmentLessThanAndActiveTrue(Integer maxEnrollment);

    @Query(value = "{ 'active': true }", fields = "{ 'groupCode': 1, 'subject': 1, 'currentEnrollment': 1, 'maxCapacity': 1 }")
    List<Group> findGroupsWithOccupancy();
}
