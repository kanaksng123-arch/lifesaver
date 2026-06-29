package kanak.project.lifesaver.service;

import kanak.project.lifesaver.dto.TaskRequest;
import kanak.project.lifesaver.model.Task;
import kanak.project.lifesaver.model.User;
import kanak.project.lifesaver.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    // ── Get all tasks for logged-in user ──
    public List<Task> getAllTasks(String email) {
        User user = userService.getUserByEmail(email);
        return taskRepository.findByUserId(user.getId());
    }

    // ── Get only pending tasks ──
    public List<Task> getPendingTasks(String email) {
        User user = userService.getUserByEmail(email);
        return taskRepository.findByUserIdAndDone(user.getId(), false);
    }

    // ── Get only completed tasks ──
    public List<Task> getCompletedTasks(String email) {
        User user = userService.getUserByEmail(email);
        return taskRepository.findByUserIdAndDone(user.getId(), true);
    }

    // ── Create a new task ──
    public Task createTask(String email, TaskRequest request) {
        User user = userService.getUserByEmail(email);

        Task task = new Task();
        task.setUserId(user.getId());
        task.setTitle(request.getTitle());
        task.setNotes(request.getNotes());
        task.setCategory(request.getCategory());
        task.setPriority(request.getPriority());
        task.setDeadline(request.getDeadline());

        return taskRepository.save(task);
    }

    // ── Update an existing task ──
    public Task updateTask(String email, String taskId, TaskRequest request) {
        Task task = getTaskAndVerifyOwner(email, taskId);

        task.setTitle(request.getTitle());
        task.setNotes(request.getNotes());
        task.setCategory(request.getCategory());
        task.setPriority(request.getPriority());
        task.setDeadline(request.getDeadline());

        return taskRepository.save(task);
    }

    // ── Mark task as done or undone ──
    public Task toggleDone(String email, String taskId) {
        Task task = getTaskAndVerifyOwner(email, taskId);

        task.setDone(!task.isDone());
        task.setCompletedAt(task.isDone() ? LocalDateTime.now() : null);

        return taskRepository.save(task);
    }

    // ── Delete a task ──
    public void deleteTask(String email, String taskId) {
        Task task = getTaskAndVerifyOwner(email, taskId);
        taskRepository.delete(task);
    }

    // ── Get overdue tasks ──
    public List<Task> getOverdueTasks(String email) {
        User user = userService.getUserByEmail(email);
        return taskRepository.findByUserIdAndDeadlineBefore(user.getId(), LocalDateTime.now())
                .stream()
                .filter(t -> !t.isDone())
                .toList();
    }

    // ── Analytics: completion rate ──
    public double getCompletionRate(String email) {
        List<Task> all = getAllTasks(email);
        if (all.isEmpty()) return 0.0;
        long done = all.stream().filter(Task::isDone).count();
        return (double) done / all.size() * 100;
    }

    // ── Private helper: fetch task and make sure it belongs to this user ──
    private Task getTaskAndVerifyOwner(String email, String taskId) {
        User user = userService.getUserByEmail(email);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Security check — user cannot touch another user's tasks
        if (!task.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return task;
    }
}