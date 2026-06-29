package kanak.project.lifesaver.controller;

import kanak.project.lifesaver.model.Habit;
import kanak.project.lifesaver.service.HabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;

    // GET /api/habits
    @GetMapping
    public ResponseEntity<List<Habit>> getAllHabits(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(habitService.getAllHabits(userDetails.getUsername()));
    }

    // POST /api/habits
    @PostMapping
    public ResponseEntity<Habit> createHabit(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {
        Habit habit = habitService.createHabit(
                userDetails.getUsername(),
                body.get("title"),
                body.get("frequency")
        );
        return ResponseEntity.ok(habit);
    }

    // PATCH /api/habits/{id}/checkin
    @PatchMapping("/{id}/checkin")
    public ResponseEntity<Habit> checkIn(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        Habit habit = habitService.checkIn(userDetails.getUsername(), id);
        return ResponseEntity.ok(habit);
    }

    // DELETE /api/habits/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteHabit(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        habitService.deleteHabit(userDetails.getUsername(), id);
        return ResponseEntity.ok(Map.of("message", "Habit deleted"));
    }
}
