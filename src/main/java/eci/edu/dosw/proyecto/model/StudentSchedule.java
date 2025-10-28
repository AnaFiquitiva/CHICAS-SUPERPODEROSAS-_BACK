package eci.edu.dosw.proyecto.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Clase que representa el horario de un estudiante.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "student_schedules")
public class StudentSchedule {
    @Id
    private String id;

    @DBRef
    private Student student;

    @DBRef
    private List<Group> enrolledGroups;

    private String academicPeriod;
    private Integer academicYear;
    private LocalDateTime createdAt;
    public List<Schedule> getSchedules() {
        if (enrolledGroups == null) {
            return List.of();
        }
        return enrolledGroups.stream()
                .flatMap(group -> group.getSchedules().stream())
                .collect(java.util.stream.Collectors.toList());
    }
}