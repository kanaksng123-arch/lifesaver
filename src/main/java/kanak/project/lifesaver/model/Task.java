package kanak.project.lifesaver.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    private String userId;          // which user owns this task

    private String title;

    private String notes;

    private String category;        // Academic, Work, Personal, Financial, Health

    private String priority;        // LOW, MEDIUM, HIGH, CRITICAL

    private LocalDateTime deadline;

    private boolean done = false;

    private LocalDateTime completedAt;

    private LocalDateTime createdAt = LocalDateTime.now();
}
