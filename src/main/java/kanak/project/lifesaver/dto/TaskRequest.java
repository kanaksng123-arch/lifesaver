package kanak.project.lifesaver.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String notes;

    @NotBlank(message = "Category is required")
    private String category;     // Academic, Work, Personal, Financial, Health

    private String priority;     // LOW, MEDIUM, HIGH, CRITICAL

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    private LocalDateTime deadline;
}
