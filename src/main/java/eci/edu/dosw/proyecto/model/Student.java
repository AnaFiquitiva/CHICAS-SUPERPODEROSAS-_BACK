package eci.edu.dosw.proyecto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

@Document(collection = "student")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    private String id; // Mongo genera ObjectId automáticamente

    private String code;
    private String name;
    private String email;

    // Referencia a otros documentos (Schedule)
    @DBRef
    private List<Schedule> schedules;
}
