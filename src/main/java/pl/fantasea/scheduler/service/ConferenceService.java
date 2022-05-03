package pl.fantasea.scheduler.service;

import org.springframework.stereotype.Service;
import pl.fantasea.scheduler.exception.*;
import pl.fantasea.scheduler.model.User;
import pl.fantasea.scheduler.repository.ConferenceRepository;
import pl.fantasea.scheduler.repository.UserRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Service
public class ConferenceService {

    private final ConferenceRepository conferenceRepository;
    private final UserRepository userRepository;

    public ConferenceService(ConferenceRepository conferenceRepository, UserRepository userRepository) {
        this.conferenceRepository = conferenceRepository;
        this.userRepository = userRepository;
    }

    public boolean registerUser(String email, String login, Long conferenceId) {

        var conference = conferenceRepository.findById(conferenceId).orElseThrow(() -> new ConferenceNotFoundException(conferenceId));

        if (conference.getRegisteredUsers().size() >= conference.getMaxParticipants()) {
            throw new ConferenceUserLimitExceededException("Conference user limit exceeded - " + conference.getMaxParticipants());
        }

        var dbUser = userRepository.findByLogin(login);

        User user;

        if (dbUser.isPresent()) {
            user = dbUser.get();

            if (user.getLogin().equals(login) && !user.getEmail().equals(email)) {
                throw new UserLoginAlreadyTakenException();
            }
            if (conference.getRegisteredUsers().stream().anyMatch(usr -> usr.getLogin().equals(login))) {
                throw new ConferenceUserAlreadyRegisteredException("User already registered");
            }
        } else {
            user = new User(login, email, Set.of(conference));
            userRepository.save(user);
        }

        String sb = "data wyslania: " + LocalDateTime
                .now()
                .truncatedTo(ChronoUnit.SECONDS) + ", do: " + user.getEmail() + ", tresc: " + "Witaj " + user.getLogin() + "! Zostałeś pomyślnie " + "zarejestrowany do konferencji " + conference.getName();

        return sendEmail(sb);
    }

    public boolean unregisterUser(String login, String email, Long conferenceId) {

        var dbUser = userRepository.findByLoginAndEmail(login, email);

        if (dbUser.isEmpty()) {
            throw new UserLoginEmailMismatchException("User login or email mismatch");
        }

        var user = dbUser.get();
        var conference = conferenceRepository.findById(conferenceId).orElseThrow(() -> new ConferenceNotFoundException(conferenceId));

        if (!conference.getRegisteredUsers().removeIf(usr -> usr.getLogin().equals(user.getLogin()))) {
            throw new ConferenceUserNotRegisteredException("User not registered");
        }

        conferenceRepository.save(conference);

        String sb = "data wyslania: " + LocalDateTime
                .now()
                .truncatedTo(ChronoUnit.SECONDS) + ", do: " + user.getEmail() + ", tresc: " + "Witaj " + user.getLogin() + "! Zostałeś pomyślnie " + "wypisany z konferencji " + conference.getName();

        return sendEmail(sb);
    }

    public boolean sendEmail(String message) {
        if (message.isEmpty() || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        try (FileWriter fw = new FileWriter("powiadomienia.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println(message);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}