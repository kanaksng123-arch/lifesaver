package kanak.project.lifesaver.controller;

import kanak.project.lifesaver.dto.TaskRequest;
import kanak.project.lifesaver.model.Task;
import kanak.project.lifesaver.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // GET /api/tasks — get all tasks
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.getAllTasks(userDetails.getUsername()));
    }

    // GET /api/tasks/pending
    @GetMapping("/pending")
    public ResponseEntity<List<Task>> getPendingTasks(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.getPendingTasks(userDetails.getUsername()));
    }

    // GET /api/tasks/completed
    @GetMapping("/completed")
    public ResponseEntity<List<Task>> getCompletedTasks(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.getCompletedTasks(userDetails.getUsername()));
    }

    // GET /api/tasks/overdue
    @GetMapping("/overdue")
    public ResponseEntity<List<Task>> getOverdueTasks(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.getOverdueTasks(userDetails.getUsername()));
    }

    // GET /api/tasks/analytics
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        double completionRate = taskService.getCompletionRate(email);
        int pending = taskService.getPendingTasks(email).size();
        int overdue = taskService.getOverdueTasks(email).size();

        return ResponseEntity.ok(Map.of(
                "completionRate", completionRate,
                "pendingCount",   pending,
                "overdueCount",   overdue
        ));
    }

    // POST /api/tasks — create task
    @PostMapping
    public ResponseEntity<Task> createTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TaskRequest request) {
        Task task = taskService.createTask(userDetails.getUsername(), request);
        return ResponseEntity.ok(task);
    }

    // PUT /api/tasks/{id} — update task
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody TaskRequest request) {
        Task task = taskService.updateTask(userDetails.getUsername(), id, request);
        return ResponseEntity.ok(task);
    }

    // PATCH /api/tasks/{id}/done — toggle done
    @PatchMapping("/{id}/done")
    public ResponseEntity<Task> toggleDone(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        Task task = taskService.toggleDone(userDetails.getUsername(), id);
        return ResponseEntity.ok(task);
    }

    // DELETE /api/tasks/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        taskService.deleteTask(userDetails.getUsername(), id);
        return ResponseEntity.ok(Map.of("message", "Task deleted successfully"));
    }
}
