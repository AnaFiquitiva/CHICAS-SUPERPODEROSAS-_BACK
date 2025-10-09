package eci.edu.dosw.proyecto.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "students")
/*
 * Entidad que representa un Estudiante en el sistema
 */
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
    private List<AcademicPlan> academicPlans;
    private List<Enrollment> currentEnrollments;

}