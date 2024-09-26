package TimeTracker.TimeTracker.controllers;

import TimeTracker.TimeTracker.models.User;
import TimeTracker.TimeTracker.services.UserService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public ResponseEntity<?> index(@RequestParam(defaultValue = "") String name) {
        if (name.isEmpty()) return new ResponseEntity<>("Не задано имя пользователя", HttpStatus.BAD_REQUEST );
        if (name.length()<2) return new ResponseEntity<>("Некорректное имя пользователя", HttpStatus.BAD_REQUEST );
        if (userService.findByNameEquals(name) != null) return new ResponseEntity<>("Такой пользователь уже существует", HttpStatus.BAD_REQUEST );

        User newUser = new User(name);
        userService.save(newUser);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @GetMapping("/userchange")
    public ResponseEntity<?> index2(@RequestParam(value = "oldname", defaultValue = "") String oldname,
                                    @RequestParam(value = "newname", defaultValue = "") String newname) {
        if (oldname.isEmpty()) return new ResponseEntity<>("Не задано имя пользователя", HttpStatus.BAD_REQUEST );
        if (userService.findByNameEquals(oldname) == null) return new ResponseEntity<>("Такого пользователя нет в базе", HttpStatus.BAD_REQUEST );

        if (newname.length()<2) return new ResponseEntity<>("Некорректное новое имя пользователя", HttpStatus.BAD_REQUEST );
        if (userService.findByNameEquals(newname) != null) return new ResponseEntity<>("Пользователь с новым именем уже существует", HttpStatus.BAD_REQUEST );

        User updatedUser = userService.findByNameEquals(oldname);
        updatedUser.setName(newname);
        userService.update(userService.findByNameEquals(oldname).getId(), updatedUser);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
}
