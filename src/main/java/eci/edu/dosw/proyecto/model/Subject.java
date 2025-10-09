package eci.edu.dosw.proyecto.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Data
@Document(collection = "subjects")

/**
  Materia académica
 */
public class Subject {
    @Id
    private String id;
    private String code;
    private String name;
    private Integer credits;
    private Boolean mandatory;
    private List<Group> groups;
    private List<String> academicPlanIds;
}