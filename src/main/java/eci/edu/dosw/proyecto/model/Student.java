package eci.edu.dosw.proyecto.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "students")
public class Student {

    @Id
    private String id;


    private String studentCode;
    private String name;
    private String email;
    private String institutionalEmail;
    private String address;
    private String phoneNumber;
    private StudentType type;
    private String program;
    private Integer currentSemester;
    private AcademicStatus status;
    private Faculty faculty;

    private List<AcademicPlan> academicPlans;
    private List<Enrollment> currentEnrollments;


    @DBRef
    private List<Schedule> schedules; // Para vincular los horarios del estudiante
}