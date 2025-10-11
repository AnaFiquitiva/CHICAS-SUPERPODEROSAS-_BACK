package eci.edu.dosw.proyecto.model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;


@Data
@Document(collection = "professors")
public class Professor {
    @Id
    private String id;
    private String name;
    private String identification;
    private String institutionalEmail;
    private String email;
    private String phone;
    private String address;


    private String facultyId;
    private List<String> subjectIds;
    private boolean active = true;
}
