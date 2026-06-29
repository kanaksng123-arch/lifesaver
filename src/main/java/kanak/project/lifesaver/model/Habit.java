package kanak.project.lifesaver.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "habits")
public class Habit {

    @Id
    private String id;

    private String userId;

    private String title;           // e.g. "Read for 30 minutes"

    private String frequency;       // DAILY, WEEKLY

    private int currentStreak = 0;

    private int longestStreak = 0;

    private List<LocalDate> checkIns = new ArrayList<>(); // dates checked in

    private LocalDateTime createdAt = LocalDateTime.now();
}
