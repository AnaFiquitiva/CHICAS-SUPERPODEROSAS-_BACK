package eci.edu.dosw.proyecto.CHICAS_SUPERPODEROSAS__BACK;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ChicasSuperpoderosasBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChicasSuperpoderosasBackApplication.class, args);
    }
}
