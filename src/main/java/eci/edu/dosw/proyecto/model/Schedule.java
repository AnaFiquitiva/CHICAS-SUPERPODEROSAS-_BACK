package eci.edu.dosw.proyecto.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "schedules")
public class Schedule {

    @Id
    private String id;


    private List<DaySchedule> daySchedules;


    private String day;
    private LocalTime startTime;
    private LocalTime endTime;

    @DBRef
    private Student student;

    @DBRef
    private Course course;

    @DBRef
    private Classroom classroom;

    @DBRef
    private Semester semester;


    public boolean hasConflict(Schedule other) {
        if (daySchedules == null || other.getDaySchedules() == null) {
            return false;
        }
        return daySchedules.stream()
                .anyMatch(day -> other.getDaySchedules().stream()
                        .anyMatch(day::hasTimeConflict));
    }
}