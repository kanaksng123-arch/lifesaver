package kanak.project.lifesaver.controller;

import kanak.project.lifesaver.dto.AiRequest;
import kanak.project.lifesaver.model.Task;
import kanak.project.lifesaver.service.GeminiService;
import kanak.project.lifesaver.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final GeminiService geminiService;
    private final TaskService taskService;

    // POST /api/ai/prioritize — AI prioritizes your tasks
    @PostMapping("/prioritize")
    public ResponseEntity<Map<String, String>> prioritize(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<Task> tasks = taskService.getPendingTasks(userDetails.getUsername());

        if (tasks.isEmpty()) {
            return ResponseEntity.ok(Map.of("result", "You have no pending tasks. Great job!"));
        }

        String result = geminiService.prioritizeTasks(tasks);
        return ResponseEntity.ok(Map.of("result", result));
    }

    // POST /api/ai/now — what should I do right now
    @PostMapping("/now")
    public ResponseEntity<Map<String, String>> whatNow(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AiRequest request) {
        List<Task> tasks = taskService.getPendingTasks(userDetails.getUsername());

        if (tasks.isEmpty()) {
            return ResponseEntity.ok(Map.of("result", "No pending tasks. Take a break, you earned it!"));
        }

        int energy = request.getEnergyLevel() != null ? request.getEnergyLevel() : 2;
        String result = geminiService.whatToDoNow(tasks, energy);
        return ResponseEntity.ok(Map.of("result", result));
    }

    // POST /api/ai/plan — build full day plan
    @PostMapping("/plan")
    public ResponseEntity<Map<String, String>> buildPlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AiRequest request) {
        List<Task> tasks = taskService.getPendingTasks(userDetails.getUsername());

        if (tasks.isEmpty()) {
            return ResponseEntity.ok(Map.of("result", "No pending tasks to plan for!"));
        }

        int hours = request.getAvailableHours() != null ? request.getAvailableHours() : 8;
        String result = geminiService.buildDayPlan(tasks, hours);
        return ResponseEntity.ok(Map.of("result", result));
    }

    // POST /api/ai/chat — free chat with Gemini about your tasks
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AiRequest request) {
        List<Task> tasks = taskService.getPendingTasks(userDetails.getUsername());
        String result = geminiService.chat(tasks, request.getMessage());
        return ResponseEntity.ok(Map.of("result", result));
    }
}
