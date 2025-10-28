package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaitingListRepository extends MongoRepository<WaitingList, String> {
    Optional<WaitingList> findByGroup(Group group);

    @Query("{ 'group.id': ?0 }")
    Optional<WaitingList> findByGroupId(String groupId);

    @Query("{ 'group.subject.faculty.id': ?0 }")
    List<WaitingList> findByFacultyId(String facultyId);

    @Query("{ 'entries.student.id': ?0 }")
    List<WaitingList> findByStudentIdInWaitingList(String studentId);

    @Query("{ 'group.active': true, 'entries.active': true }")
    List<WaitingList> findActiveWaitingLists();
}

