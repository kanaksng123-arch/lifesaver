package kanak.project.lifesaver.dto;

import lombok.Data;

@Data
public class AiRequest {
    private String message;       // for free chat with Gemini
    private Integer energyLevel;  // 1=low, 2=medium, 3=high — for mood-aware planning
    private Integer availableHours; // how many hours user has today
}
