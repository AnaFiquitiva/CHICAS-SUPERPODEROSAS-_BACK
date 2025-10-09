package eci.edu.dosw.proyecto.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@Document(collection = "groups")

/**
 * Grupo de una materia
 */
public class Group {
    @Id
    private String id;
    private String subjectId;
    private String groupCode;
    private String professor;
    private Schedule schedule;
    private Integer maxCapacity;
    private Integer currentEnrollment;
    private Integer waitingListCount;
    private Boolean active;

}