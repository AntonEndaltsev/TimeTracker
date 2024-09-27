package TimeTracker.TimeTracker.services;

import TimeTracker.TimeTracker.models.Task;
import TimeTracker.TimeTracker.models.User;
import TimeTracker.TimeTracker.repositories.TaskRepository;
import TimeTracker.TimeTracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findOne(int id){
        Optional<Task> foundTask = taskRepository.findById(id);
        return foundTask.orElse((null));
    }

    public Task findByName(String name){
        Optional<Task> foundTask = taskRepository.findByName(name);
        return foundTask.orElse((null));
    }

    @Transactional
    public void save(Task task){
        taskRepository.save(task);
    }

    @Transactional
    public void update(int id, Task updatedTask){
        updatedTask.setId(id);
        taskRepository.save(updatedTask);
    }

    @Transactional
    public void delete(int id){

        taskRepository.deleteById(id);
    }
}
