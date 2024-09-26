package TimeTracker.TimeTracker.controllers;

import TimeTracker.TimeTracker.DTO.UserDTO;
import TimeTracker.TimeTracker.models.Task;
import TimeTracker.TimeTracker.DTO.TaskDTO;
import TimeTracker.TimeTracker.models.User;
import TimeTracker.TimeTracker.services.TaskService;
import TimeTracker.TimeTracker.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class UserController {
    private final UserService userService;
    private final TaskService taskService;

    ObjectMapper objectMapper = new ObjectMapper();
    ModelMapper modelMapper = new ModelMapper();

    public UserController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @GetMapping("/user")
    public ResponseEntity<?> adduser(@RequestParam(defaultValue = "") String name) {
        if (name.isEmpty()) return new ResponseEntity<>("Не задано имя пользователя", HttpStatus.BAD_REQUEST );
        if (name.length()<2) return new ResponseEntity<>("Некорректное имя пользователя", HttpStatus.BAD_REQUEST );
        if (userService.findByNameEquals(name) != null) return new ResponseEntity<>("Такой пользователь уже существует", HttpStatus.BAD_REQUEST );

        User newUser = new User(name);
        userService.save(newUser);
        return new ResponseEntity<>(modelMapper.map(newUser, UserDTO.class), HttpStatus.OK);
    }

    @GetMapping("/userchange")
    public ResponseEntity<?> updateuser(@RequestParam(value = "oldname", defaultValue = "") String oldname,
                                    @RequestParam(value = "newname", defaultValue = "") String newname) {
        if (oldname.isEmpty()) return new ResponseEntity<>("Не задано имя пользователя", HttpStatus.BAD_REQUEST );
        if (userService.findByNameEquals(oldname) == null) return new ResponseEntity<>("Такого пользователя нет в базе", HttpStatus.BAD_REQUEST );

        if (newname.length()<2) return new ResponseEntity<>("Некорректное новое имя пользователя", HttpStatus.BAD_REQUEST );
        if (userService.findByNameEquals(newname) != null) return new ResponseEntity<>("Пользователь с новым именем уже существует", HttpStatus.BAD_REQUEST );

        User updatedUser = userService.findByNameEquals(oldname);
        updatedUser.setName(newname);
        userService.update(userService.findByNameEquals(oldname).getId(), updatedUser);
        return new ResponseEntity<>(modelMapper.map(updatedUser, UserDTO.class), HttpStatus.OK);
    }

    @GetMapping("/starttask")
    public ResponseEntity<?> addnewtask(@RequestParam(value = "name", defaultValue = "") String name,
                                    @RequestParam(value = "user", defaultValue = "") String user) {
        if (name.isEmpty()) return new ResponseEntity<>("Не задано имя задачи", HttpStatus.BAD_REQUEST );
        if (name.length()<2) return new ResponseEntity<>("Некорректное имя задачи", HttpStatus.BAD_REQUEST );
        if (taskService.findByName(name)!=null) return new ResponseEntity<>("Такая задача уже существует", HttpStatus.BAD_REQUEST );

        if (user.isEmpty()) return new ResponseEntity<>("Не задано имя пользователя", HttpStatus.BAD_REQUEST );
        if (userService.findByNameEquals(user) == null) return new ResponseEntity<>("Такого пользователя нет в базе", HttpStatus.BAD_REQUEST );

        //if (newname.length()<2) return new ResponseEntity<>("Некорректное новое имя пользователя", HttpStatus.BAD_REQUEST );
        //if (userService.findByNameEquals(newname) != null) return new ResponseEntity<>("Пользователь с новым именем уже существует", HttpStatus.BAD_REQUEST );

        User owner = userService.findByNameEquals(user);

        Task task = new Task(name,owner,LocalDateTime.now(),LocalDateTime.now());
        taskService.save(task);
        TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);
        taskDTO.setOwner_id(owner.getId());
        return new ResponseEntity<>(taskDTO, HttpStatus.OK);
    }

    @GetMapping("/stoptask")
    public ResponseEntity<?> stoptask(@RequestParam(value = "name", defaultValue = "") String name){
        if (name.isEmpty()) return new ResponseEntity<>("Не задано имя задачи для завершения", HttpStatus.BAD_REQUEST );
        if (taskService.findByName(name)==null) return new ResponseEntity<>("Такой задачи нет в базе", HttpStatus.BAD_REQUEST );
        Task foundTask = taskService.findByName(name);
        foundTask.setEndTime(LocalDateTime.now());

        taskService.save(foundTask);
        TaskDTO taskDTO = modelMapper.map(foundTask, TaskDTO.class);
        taskDTO.setOwner_id(foundTask.getOwner().getId());
        return new ResponseEntity<>(taskDTO, HttpStatus.OK);
    }



}
