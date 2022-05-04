package pl.fantasea.scheduler.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.fantasea.scheduler.dto.UserDto;
import pl.fantasea.scheduler.dto.UserUpdateMailDto;
import pl.fantasea.scheduler.exception.UserEmailAlreadyInUseException;
import pl.fantasea.scheduler.exception.UserLoginEmailMismatchException;
import pl.fantasea.scheduler.model.User;
import pl.fantasea.scheduler.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PutMapping("/change-email")
    public ResponseEntity<String> getUsers(@RequestBody @Valid UserUpdateMailDto body) {
        var user = new User(body.getLogin().toLowerCase().trim(),
                            body.getEmail().toLowerCase().trim());

        try {
            if (userService.updateEmail(user, body.getNewEmail().toLowerCase().trim())) {
                return ResponseEntity.ok("Pomyślnie zmieniono email na: " + body.getEmail());
            } else {
                return ResponseEntity.badRequest().body("Nie udało się zmienić emaila");
            }
        } catch (UserLoginEmailMismatchException | IllegalArgumentException |UserEmailAlreadyInUseException e ) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

}
