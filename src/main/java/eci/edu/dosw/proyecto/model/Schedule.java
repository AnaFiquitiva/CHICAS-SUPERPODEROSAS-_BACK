package eci.edu.dosw.proyecto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalTime;

@Document(collection = "schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    private String id;

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
}
