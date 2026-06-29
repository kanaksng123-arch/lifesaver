package kanak.project.lifesaver.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;        // BCrypt hashed

    private String name;

    private String timezone = "Asia/Kolkata";

    private Integer workStartHour = 9;
    private Integer workEndHour = 21;

    private LocalDateTime createdAt = LocalDateTime.now();

    private List<String> roles = List.of("ROLE_USER");
}