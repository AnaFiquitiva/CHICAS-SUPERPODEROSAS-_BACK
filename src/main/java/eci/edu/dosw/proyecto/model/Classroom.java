package eci.edu.dosw.proyecto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Document(collection = "classroom")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classroom {

    @Id
    private String id; // Mongo genera el ObjectId automáticamente

    private String building;
    private String roomNumber;
    private int capacity;
}

