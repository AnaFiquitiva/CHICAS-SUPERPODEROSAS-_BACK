package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.model.WaitingList;
import eci.edu.dosw.proyecto.model.WaitingListEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaitingListEntryRepository extends MongoRepository<WaitingListEntry, String> {
    List<WaitingListEntry> findByWaitingListAndActiveTrueOrderByPositionAsc(WaitingList waitingList);

    List<WaitingListEntry> findByStudentAndActiveTrue(Student student);

    @Query("{ 'waitingList.id': ?0, 'active': true }")
    List<WaitingListEntry> findByWaitingListIdAndActiveTrueOrderByPositionAsc(String waitingListId);

    @Query("{ 'student.id': ?0, 'active': true }")
    List<WaitingListEntry> findByStudentIdAndActiveTrue(String studentId);

    @Query(value = "{ 'waitingList.id': ?0, 'active': true }", count = true)
    long countActiveEntriesByWaitingListId(String waitingListId);

    Optional<WaitingListEntry> findByWaitingListAndStudentAndActiveTrue(WaitingList waitingList, Student student);
}
