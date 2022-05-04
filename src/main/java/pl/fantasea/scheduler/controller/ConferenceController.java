package pl.fantasea.scheduler.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.fantasea.scheduler.dto.SubjectInterest;
import pl.fantasea.scheduler.dto.UserDto;
import pl.fantasea.scheduler.exception.*;
import pl.fantasea.scheduler.model.Conference;
import pl.fantasea.scheduler.model.User;
import pl.fantasea.scheduler.service.ConferenceService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/conferences")
public class ConferenceController {
    private final ConferenceService conferenceService;

    @GetMapping("/all")
    public ResponseEntity<List<Conference>> getAllConferences() {
        return ResponseEntity.ok(conferenceService.getAllConferences());
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<String> registerUser(@PathVariable Long id, @RequestBody @Valid UserDto body) {
        var user = new User(body.getLogin().toLowerCase().trim(), body.getEmail().toLowerCase().trim());

        try {
            if (conferenceService.registerUser(user, id)) {
                return ResponseEntity.ok("Zarejestrowano na konferencje - " + id);
            } else {
                return ResponseEntity.badRequest().body("Nie udało sie zarejestrowac na konferencje - " + id);
            }
        } catch (ConferenceUserLimitExceededException | ConferenceUserAlreadyRegisteredException |
                 ConferenceUserNotEligibleException | UserLoginAlreadyTakenException | ConferenceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/unregister")
    public ResponseEntity<String> unregisterUser(@PathVariable Long id, @RequestBody @Valid UserDto body) {
        var user = new User(body.getLogin().toLowerCase().trim(), body.getEmail().toLowerCase().trim());

        try {
            if (conferenceService.unregisterUser(user, id)) {
                return ResponseEntity.ok("Wypisano z konferencji - " + id);
            } else {
                return ResponseEntity.badRequest().body("Nie udało się wypisać z konferencji - " + id);
            }
        } catch (UserLoginEmailMismatchException | ConferenceNotFoundException | ConferenceUserNotRegisteredException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/interest")
    public ResponseEntity<String> getInterest(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(String.valueOf(conferenceService.getPercentageInterest(id)));
        } catch (ConferenceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/subject-interest")
    public ResponseEntity<List<SubjectInterest>> getSubjectInterest() {
        return ResponseEntity.ok(conferenceService.getTotalSubjectInterest());
    }
}
