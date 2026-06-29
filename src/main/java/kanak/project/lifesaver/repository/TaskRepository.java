package kanak.project.lifesaver.repository;

import kanak.project.lifesaver.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByUserId(String userId);
    List<Task> findByUserIdAndDone(String userId, boolean done);
    List<Task> findByUserIdAndDeadlineBefore(String userId, LocalDateTime dateTime);
}
