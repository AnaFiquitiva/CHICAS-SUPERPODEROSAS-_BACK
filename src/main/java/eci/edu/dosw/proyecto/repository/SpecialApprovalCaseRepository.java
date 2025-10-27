package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.SpecialApprovalCase;
import eci.edu.dosw.proyecto.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SpecialApprovalCaseRepository extends MongoRepository<SpecialApprovalCase, String> {
    List<SpecialApprovalCase> findByApprovedBy(User approvedBy);

    @Query("{ 'approvedBy.id': ?0 }")
    List<SpecialApprovalCase> findByApprovedById(String approvedById);

    @Query("{ 'request.student.program.faculty.id': ?0 }")
    List<SpecialApprovalCase> findByFacultyId(String facultyId);

    @Query("{ 'approvedAt': { $gte: ?0, $lte: ?1 } }")
    List<SpecialApprovalCase> findByApprovedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("{ 'constraintsOverridden': { $regex: ?0, $options: 'i' } }")
    List<SpecialApprovalCase> findByConstraintsOverriddenContaining(String constraint);
}
