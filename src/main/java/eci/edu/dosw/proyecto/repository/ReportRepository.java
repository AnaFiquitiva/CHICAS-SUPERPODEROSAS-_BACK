package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.dto.GroupDemandResponse;
import eci.edu.dosw.proyecto.dto.ReportRequest;
import eci.edu.dosw.proyecto.model.Report;
import eci.edu.dosw.proyecto.model.Request;
import eci.edu.dosw.proyecto.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {
    List<Report> findByGeneratedBy(User user);

    List<Report> findByReportType(String reportType);

    @Query("{ 'generatedBy.id': ?0 }")
    List<Report> findByGeneratedById(String userId);

    @Query("{ 'faculty.id': ?0 }")
    List<Report> findByFacultyId(String facultyId);

    @Query("{ 'reportType': ?0, 'generatedAt': { $gte: ?1, $lte: ?2 } }")
    List<Report> findByReportTypeAndGeneratedAtBetween(String reportType, LocalDateTime start, LocalDateTime end);

    @Query("{ 'faculty.id': ?0, 'reportType': ?1, 'generatedAt': { $gte: ?2, $lte: ?3 } }")
    List<Report> findByFacultyIdAndReportTypeAndGeneratedAtBetween(String facultyId, String reportType, LocalDateTime start, LocalDateTime end);

    @Query("""
{
  'student.program.faculty.id': ?#{#request.facultyId},
  'createdAt': { $gte: ?#{#request.startDate}, $lte: ?#{#request.endDate} }
}
""")
    List<Request> getMostRequestedGroups(@Param("request") ReportRequest request);

}
