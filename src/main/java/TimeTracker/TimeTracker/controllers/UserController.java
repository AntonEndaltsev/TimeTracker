package TimeTracker.TimeTracker.controllers;

import TimeTracker.TimeTracker.models.User;
import TimeTracker.TimeTracker.services.UserService;
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
    public ResponseEntity<?> index(@RequestParam(required = true) String name) {
        User newUser = new User(name);
        if (name != null) userService.save(newUser);

        return new ResponseEntity<>(newUser, HttpStatus.OK);



    }
}
