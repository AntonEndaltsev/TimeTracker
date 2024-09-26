package TimeTracker.TimeTracker.repositories;

import TimeTracker.TimeTracker.models.Task;
import TimeTracker.TimeTracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    Optional<Task> findByName(String name);


}
