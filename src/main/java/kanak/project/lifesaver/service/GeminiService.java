package kanak.project.lifesaver.service;

import kanak.project.lifesaver.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    // ── Core method: send a prompt to Gemini, get text back ──
    public String askGemini(String prompt) {
        // Build the request body Gemini expects
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        // Call Gemini API using WebClient
        Map response = webClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block(); // blocking call — waits for response

        // Extract the text from Gemini's response structure
        try {
            List candidates = (List) response.get("candidates");
            Map firstCandidate = (Map) candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List parts = (List) content.get("parts");
            Map firstPart = (Map) parts.get(0);
            return (String) firstPart.get("text");
        } catch (Exception e) {
            return "Gemini could not generate a response. Please try again.";
        }
    }

    // ── Build task summary string for prompts ──
    private String buildTaskSummary(List<Task> tasks) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        StringBuilder sb = new StringBuilder();
        for (Task t : tasks) {
            sb.append("- \"").append(t.getTitle()).append("\"")
                    .append(" | Category: ").append(t.getCategory())
                    .append(" | Deadline: ").append(t.getDeadline().format(formatter))
                    .append(" | Priority: ").append(t.getPriority() != null ? t.getPriority() : "Not set")
                    .append(t.getNotes() != null ? " | Notes: " + t.getNotes() : "")
                    .append("\n");
        }
        return sb.toString();
    }

    // ── Prioritize tasks ──
    public String prioritizeTasks(List<Task> tasks) {
        String taskList = buildTaskSummary(tasks);
        String prompt = """
                You are a productivity AI assistant. Current time: %s.

                The user has these pending tasks:
                %s

                Prioritize these tasks with a numbered list.
                For each task:
                1. State clearly why it is urgent or important
                2. Give one specific immediate action to start it right now

                Be direct, motivating, and practical. No fluff.
                """.formatted(LocalDateTime.now(), taskList);

        return askGemini(prompt);
    }

    // ── What to do right now ──
    public String whatToDoNow(List<Task> tasks, int energyLevel) {
        String taskList = buildTaskSummary(tasks);
        String energy = energyLevel == 1 ? "low energy" : energyLevel == 2 ? "medium energy" : "high energy";

        String prompt = """
                You are a productivity AI. Current time: %s.
                The user currently has %s.

                Pending tasks:
                %s

                Pick the single most important task for right now given their energy level.
                Give a 4-step micro action plan they can start in the next 5 minutes.
                Be specific, practical, and energizing.
                """.formatted(LocalDateTime.now(), energy, taskList);

        return askGemini(prompt);
    }

    // ── Build full day plan ──
    public String buildDayPlan(List<Task> tasks, int availableHours) {
        String taskList = buildTaskSummary(tasks);

        String prompt = """
                You are a scheduling AI. Current time: %s.
                The user has %d hours available today.

                Pending tasks:
                %s

                Build a realistic hour-by-hour schedule for today.
                Format each block as: "9:00–10:30 AM: [Task] — [What to accomplish]"
                Consider task deadlines and priorities.
                Do not exceed the available hours.
                End with one motivational sentence.
                """.formatted(LocalDateTime.now(), availableHours, taskList);

        return askGemini(prompt);
    }

    // ── Free chat — user asks anything about their tasks ──
    public String chat(List<Task> tasks, String userMessage) {
        String taskList = buildTaskSummary(tasks);

        String prompt = """
                You are a helpful productivity AI assistant. Current time: %s.

                The user's pending tasks:
                %s

                User says: "%s"

                Respond helpfully based on their tasks and message.
                Be concise, warm, and actionable.
                """.formatted(LocalDateTime.now(), taskList, userMessage);

        return askGemini(prompt);
    }
}