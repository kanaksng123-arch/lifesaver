package kanak.project.lifesaver.service;

import kanak.project.lifesaver.model.Habit;
import kanak.project.lifesaver.model.User;
import kanak.project.lifesaver.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final UserService userService;

    // ── Get all habits for user ──
    public List<Habit> getAllHabits(String email) {
        User user = userService.getUserByEmail(email);
        return habitRepository.findByUserId(user.getId());
    }

    // ── Create a new habit ──
    public Habit createHabit(String email, String title, String frequency) {
        User user = userService.getUserByEmail(email);

        Habit habit = new Habit();
        habit.setUserId(user.getId());
        habit.setTitle(title);
        habit.setFrequency(frequency);

        return habitRepository.save(habit);
    }

    // ── Check in for today ──
    public Habit checkIn(String email, String habitId) {
        User user = userService.getUserByEmail(email);
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        if (!habit.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        LocalDate today = LocalDate.now();

        // Prevent double check-in on same day
        if (habit.getCheckIns().contains(today)) {
            throw new RuntimeException("Already checked in today");
        }

        habit.getCheckIns().add(today);

        // Recalculate streak
        habit.setCurrentStreak(calculateStreak(habit.getCheckIns()));
        if (habit.getCurrentStreak() > habit.getLongestStreak()) {
            habit.setLongestStreak(habit.getCurrentStreak());
        }

        return habitRepository.save(habit);
    }

    // ── Delete a habit ──
    public void deleteHabit(String email, String habitId) {
        User user = userService.getUserByEmail(email);
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        if (!habit.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        habitRepository.delete(habit);
    }

    // ── Calculate current streak from check-in dates ──
    private int calculateStreak(List<LocalDate> checkIns) {
        if (checkIns.isEmpty()) return 0;

        List<LocalDate> sorted = checkIns.stream().sorted().toList();
        int streak = 1;
        LocalDate today = LocalDate.now();

        // Must have checked in today or yesterday to have an active streak
        LocalDate last = sorted.get(sorted.size() - 1);
        if (!last.equals(today) && !last.equals(today.minusDays(1))) {
            return 0;
        }

        // Count backwards
        for (int i = sorted.size() - 1; i > 0; i--) {
            if (sorted.get(i).minusDays(1).equals(sorted.get(i - 1))) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }
}
