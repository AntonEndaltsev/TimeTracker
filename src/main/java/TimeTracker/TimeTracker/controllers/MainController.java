package TimeTracker.TimeTracker.controllers;

import TimeTracker.TimeTracker.DTO.Task2DTO;
import TimeTracker.TimeTracker.DTO.UserDTO;
import TimeTracker.TimeTracker.models.Task;
import TimeTracker.TimeTracker.DTO.TaskDTO;
import TimeTracker.TimeTracker.models.User;
import TimeTracker.TimeTracker.services.TaskService;
import TimeTracker.TimeTracker.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@RestController
public class MainController {
    private final UserService userService;
    private final TaskService taskService;

    ObjectMapper objectMapper = new ObjectMapper();
    ModelMapper modelMapper = new ModelMapper();

    public MainController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @GetMapping("/user")
    public ResponseEntity<?> adduser(@RequestParam(defaultValue = "") String name) {
        if (name.isEmpty()) return new ResponseEntity<>("Не задано имя пользователя", HttpStatus.BAD_REQUEST);
        if (name.length() < 2) return new ResponseEntity<>("Некорректное имя пользователя", HttpStatus.BAD_REQUEST);
        if (userService.findByNameEquals(name) != null)
            return new ResponseEntity<>("Такой пользователь уже существует", HttpStatus.BAD_REQUEST);

        User newUser = new User(name);
        userService.save(newUser);
        return new ResponseEntity<>(modelMapper.map(newUser, UserDTO.class), HttpStatus.OK);
    }

    @GetMapping("/userchange")
    public ResponseEntity<?> updateuser(@RequestParam(value = "oldname", defaultValue = "") String oldname,
                                        @RequestParam(value = "newname", defaultValue = "") String newname) {
        if (oldname.isEmpty()) return new ResponseEntity<>("Не задано имя пользователя", HttpStatus.BAD_REQUEST);
        if (userService.findByNameEquals(oldname) == null)
            return new ResponseEntity<>("Такого пользователя нет в базе", HttpStatus.BAD_REQUEST);

        if (newname.length() < 2)
            return new ResponseEntity<>("Некорректное новое имя пользователя", HttpStatus.BAD_REQUEST);
        if (userService.findByNameEquals(newname) != null)
            return new ResponseEntity<>("Пользователь с новым именем уже существует", HttpStatus.BAD_REQUEST);

        User updatedUser = userService.findByNameEquals(oldname);
        updatedUser.setName(newname);
        userService.update(userService.findByNameEquals(oldname).getId(), updatedUser);
        return new ResponseEntity<>(modelMapper.map(updatedUser, UserDTO.class), HttpStatus.OK);
    }

    @GetMapping("/starttask")
    public ResponseEntity<?> addnewtask(@RequestParam(value = "name", defaultValue = "") String name,
                                        @RequestParam(value = "user", defaultValue = "") String user) {
        if (name.isEmpty()) return new ResponseEntity<>("Не задано имя задачи", HttpStatus.BAD_REQUEST);
        if (name.length() < 2) return new ResponseEntity<>("Некорректное имя задачи", HttpStatus.BAD_REQUEST);
        if (taskService.findByName(name) != null)
            return new ResponseEntity<>("Такая задача уже существует", HttpStatus.BAD_REQUEST);

        if (user.isEmpty()) return new ResponseEntity<>("Не задано имя пользователя", HttpStatus.BAD_REQUEST);
        if (userService.findByNameEquals(user) == null)
            return new ResponseEntity<>("Такого пользователя нет в базе", HttpStatus.BAD_REQUEST);

        //if (newname.length()<2) return new ResponseEntity<>("Некорректное новое имя пользователя", HttpStatus.BAD_REQUEST );
        //if (userService.findByNameEquals(newname) != null) return new ResponseEntity<>("Пользователь с новым именем уже существует", HttpStatus.BAD_REQUEST );

        User owner = userService.findByNameEquals(user);

        //завершаем все задачи пользователя, которые остались открытыми
        finishCurrentUserTasks(owner, LocalDateTime.now());

        //добавляем новую задачу со значением endtime по умолчанию - конец дня
        Task task = new Task(name, owner, LocalDateTime.now(), LocalDateTime.of(LocalDate.now(), LocalTime.MAX).minusSeconds(1));
        taskService.save(task);
        TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);
        taskDTO.setOwner_id(owner.getId());
        return new ResponseEntity<>(taskDTO, HttpStatus.OK);
    }

    public void finishCurrentUserTasks(User owner, LocalDateTime now) {
        for (Task task : taskService.findAll()) {
            if (task.getOwner() == owner && now.isBefore(task.getEndTime())) {
                task.setEndTime(now);
                taskService.save(task);
            }
        }
    }

    @GetMapping("/stoptask")
    public ResponseEntity<?> stoptask(@RequestParam(value = "name", defaultValue = "") String name) {
        if (name.isEmpty()) return new ResponseEntity<>("Не задано имя задачи для завершения", HttpStatus.BAD_REQUEST);
        if (taskService.findByName(name) == null)
            return new ResponseEntity<>("Такой задачи нет в базе", HttpStatus.BAD_REQUEST);
        Task foundTask = taskService.findByName(name);
        foundTask.setEndTime(LocalDateTime.now());

        taskService.save(foundTask);
        TaskDTO taskDTO = modelMapper.map(foundTask, TaskDTO.class);
        taskDTO.setOwner_id(foundTask.getOwner().getId());
        return new ResponseEntity<>(taskDTO, HttpStatus.OK);
    }

    @GetMapping("/showusertasks")
    public ResponseEntity<?> showtasks(@RequestParam(value = "user", defaultValue = "") String user,
                                       @RequestParam(value = "from", defaultValue = "0") Integer start,
                                       @RequestParam(value = "to", defaultValue = "0") Integer finish) {
        if (user.isEmpty()) return new ResponseEntity<>("Не задано имя пользователя", HttpStatus.BAD_REQUEST);
        if (userService.findByNameEquals(user) == null)
            return new ResponseEntity<>("Такого пользователя нет в базе", HttpStatus.BAD_REQUEST);
        if (start >= finish || start < 0 || finish > 23)
            return new ResponseEntity<>("Некорректный диапазон", HttpStatus.BAD_REQUEST);

        List<Task2DTO> foundTasks = new ArrayList<>();
        for (Task task : taskService.findAll()) {
            if (task.getOwner() == userService.findByNameEquals(user)) {
                if (task.getStartTime().getHour() >= start && task.getEndTime().getHour() <= finish) {
                    //TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);
                    //taskDTO.setOwner_id(task.getOwner().getId());
                    Task2DTO taskDTO = new Task2DTO();
                    taskDTO.setName(task.getName());
                    taskDTO.setTime(calculateTime(task.getStartTime(), task.getEndTime()));
                    foundTasks.add(taskDTO);
                    //System.out.println(task.getName());
                }

                if (task.getStartTime().getHour() >= start && task.getEndTime().getHour() > finish && task.getStartTime().getHour() < finish) {

                    Task2DTO taskDTO = new Task2DTO();
                    taskDTO.setName(task.getName());
                    taskDTO.setTime(calculateTime(task.getStartTime(), LocalDateTime.of(LocalDate.now(), LocalTime.of(finish, 0))));
                    foundTasks.add(taskDTO);

                }

                if (task.getStartTime().getHour() < start && task.getEndTime().getHour() <= finish && task.getEndTime().getHour() > start) {

                    Task2DTO taskDTO = new Task2DTO();
                    taskDTO.setName(task.getName());
                    taskDTO.setTime(calculateTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(start, 0)), task.getEndTime()));
                    foundTasks.add(taskDTO);

                }

                if (task.getStartTime().getHour() < start && task.getEndTime().getHour() > finish) {

                    Task2DTO taskDTO = new Task2DTO();
                    taskDTO.setName(task.getName());
                    taskDTO.setTime(calculateTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(start, 0)), LocalDateTime.of(LocalDate.now(), LocalTime.of(finish, 0))));
                    foundTasks.add(taskDTO);
                }

            }
        }
        Collections.sort(foundTasks);
        return new ResponseEntity<>(foundTasks, HttpStatus.OK);
    }

    @GetMapping("/showusertime")
    public ResponseEntity<?> showtime(@RequestParam(value = "user", defaultValue = "") String user,
                                      @RequestParam(value = "from", defaultValue = "0") Integer start,
                                      @RequestParam(value = "to", defaultValue = "0") Integer finish) {
        if (user.isEmpty()) return new ResponseEntity<>("Не задано имя пользователя", HttpStatus.BAD_REQUEST);
        if (userService.findByNameEquals(user) == null)
            return new ResponseEntity<>("Такого пользователя нет в базе", HttpStatus.BAD_REQUEST);
        if (start >= finish || start < 0 || finish > 23)
            return new ResponseEntity<>("Некорректный диапазон", HttpStatus.BAD_REQUEST);

        int time = 0;
        for (Task task : taskService.findAll()) {
            if (task.getOwner() == userService.findByNameEquals(user)) {
                if (task.getStartTime().getHour() >= start && task.getEndTime().getHour() <= finish) {
                    time += task.getEndTime().getHour() * 60 + task.getEndTime().getMinute() - task.getStartTime().getHour() * 60 - task.getStartTime().getMinute();
                }

                if (task.getStartTime().getHour() >= start && task.getEndTime().getHour() > finish && task.getStartTime().getHour() < finish) {
                    time += finish * 60 - task.getStartTime().getHour() * 60 - task.getStartTime().getMinute();

                }

                if (task.getStartTime().getHour() < start && task.getEndTime().getHour() <= finish && task.getEndTime().getHour() > start) {
                    time += task.getEndTime().getHour() * 60 + task.getEndTime().getMinute() - start * 60;

                }

                if (task.getStartTime().getHour() < start && task.getEndTime().getHour() > finish) {
                    time += finish * 60 - start * 60;

                }

            }
        }
        int totalHours = time / 60;

        int totalMinutes = time - totalHours * 60;
        String answer = totalHours + ":" + totalMinutes;
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    public String calculateTime(LocalDateTime start, LocalDateTime finish) {
        int totalMinutes = finish.getHour() * 60 + finish.getMinute() - start.getHour() * 60 - start.getMinute();
        //System.out.println(totalMinutes);
        int totalHours = totalMinutes / 60;
        //System.out.println(totalHours);
        if (totalHours > 0) {
            //System.out.println(totalMinutes);
            //System.out.println(totalHours);
            totalMinutes -= totalHours * 60;
            //System.out.println(totalMinutes);
        }
        //System.out.println(totalMinutes);
        return totalHours + ":" + totalMinutes;
    }

    @GetMapping("/deleteusertasks")
    public ResponseEntity<?> deleteusertaskscontroller(@RequestParam(value = "user", defaultValue = "") String user) {
        if (user.isEmpty()) return new ResponseEntity<>("Не задано имя пользователя", HttpStatus.BAD_REQUEST);
        if (userService.findByNameEquals(user) == null)
            return new ResponseEntity<>("Такого пользователя нет в базе", HttpStatus.BAD_REQUEST);


        return new ResponseEntity<>( deleteUserTasks(user), HttpStatus.OK);
    }

    @GetMapping("/deleteuser")
    public ResponseEntity<?> deleteusercontroller(@RequestParam(value = "user", defaultValue = "") String user) {
        if (user.isEmpty()) return new ResponseEntity<>("Не задано имя пользователя", HttpStatus.BAD_REQUEST);
        if (userService.findByNameEquals(user) == null)
            return new ResponseEntity<>("Такого пользователя нет в базе", HttpStatus.BAD_REQUEST);

        List<TaskDTO> allTasksDTO = deleteUserTasks(user);
        userService.delete(userService.findByNameEquals(user).getId());
        List<UserDTO> allUserDTO = new ArrayList<>();
        for (User user1: userService.findAll())  allUserDTO.add(modelMapper.map(user1, UserDTO.class));
        return new ResponseEntity<>(allUserDTO, HttpStatus.OK);
    }

    public List<TaskDTO> deleteUserTasks(String user){
        List<Task> allTasks = taskService.findAll();
        List<TaskDTO> allTasksDTO = new ArrayList<>();
        Iterator iterator = allTasks.iterator();
        while (iterator.hasNext()) {
            Task task = (Task) iterator.next();

            if (task.getOwner() == userService.findByNameEquals(user)) {
                taskService.delete(task.getId());
            }
            else {
                TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);
                taskDTO.setOwner_id(task.getOwner().getId());
                allTasksDTO.add(taskDTO);
            }
        }
        return  allTasksDTO;
    }
}