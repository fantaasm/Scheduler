package pl.fantasea.scheduler.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.fantasea.scheduler.dto.SubjectInterest;
import pl.fantasea.scheduler.exception.*;
import pl.fantasea.scheduler.model.Conference;
import pl.fantasea.scheduler.model.User;
import pl.fantasea.scheduler.repository.ConferenceRepository;
import pl.fantasea.scheduler.repository.UserRepository;

import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ConferenceService {
    private final ConferenceRepository conferenceRepository;
    private final UserRepository userRepository;

    public boolean registerUser(@NonNull final User user, Long conferenceId) {
        var conference = conferenceRepository.findById(conferenceId)
                                                        .orElseThrow(() -> new ConferenceNotFoundException(conferenceId));

        if (conference.getRegisteredUsers().size() >= conference.getMaxParticipants())
            throw new ConferenceUserLimitExceededException(conference.getMaxParticipants());

        var dbUser = userRepository.findByLoginOrEmail(user.getLogin(), user.getEmail())
                                         .orElse(null);

        if (dbUser != null) {
            if (!dbUser.getLogin().equals(user.getLogin()) || !dbUser.getEmail().equals(user.getEmail()))
                throw new UserLoginAlreadyTakenException();

            if (conference.getRegisteredUsers().stream().anyMatch(usr -> usr.getLogin().equals(user.getLogin())))
                throw new ConferenceUserAlreadyRegisteredException(user);

            if (isNotEligible(dbUser, conference))
                throw new ConferenceUserNotEligibleException("Użytkownik nie może zapisać się na tą konferencję, ponieważ jest zapisany na " + conference.getName() + ", " + "która odbywa się w tym samym czasie");

            dbUser.getConferences().add(conference);
        } else {
            dbUser = new User(user.getLogin(), user.getEmail(), Set.of(conference));
        }

        userRepository.save(dbUser);

        var email = "Witaj " + user.getLogin() + "! Zostałeś pomyślnie " + "zarejestrowany do konferencji " + conference.getName();
        return sendEmail(dbUser, email);
    }

    private boolean isNotEligible(@NonNull User user, @NonNull Conference conference) {
        return conferenceRepository.isUserNotEligible( user.getId(),conference.getStartDate());
    }

    @Transactional
    public boolean unregisterUser(@NonNull User user, Long conferenceId) {
        var dbUser = userRepository.findByLoginAndEmail(user.getLogin(), user.getEmail())
                                         .orElseThrow(() -> new UserLoginEmailMismatchException(user));

        var conference = conferenceRepository.findById(conferenceId)
                                                        .orElseThrow(() -> new ConferenceNotFoundException(conferenceId));

        if(!dbUser.getConferences().removeIf(c -> c.getId().equals(conferenceId)))
            throw new ConferenceUserNotRegisteredException(user);

        userRepository.save(dbUser);

        var email = "Witaj " + dbUser.getLogin() + "! Zostałeś pomyślnie wypisany z konferencji " + conference.getName();
        return sendEmail(dbUser, email);
    }

    public boolean sendEmail(@NonNull User user, String message) {
        if (message.isEmpty() || message.isBlank())
            throw new IllegalArgumentException("Message cannot be empty");

        var sb = new StringBuilder("Data wyslania: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        sb.append(", do: ").append(user.getEmail());
        sb.append(", tresc: ").append(message);

        try (FileWriter fw = new FileWriter("powiadomienia.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(sb);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public float getPercentageInterest(Long conferenceId) {
        if (!conferenceRepository.existsById(conferenceId))
            throw new ConferenceNotFoundException(conferenceId);

        return conferenceRepository.findTotalInterestInConference(conferenceId);
    }

    public List<Conference> getAllConferences() {
        return Collections.unmodifiableList((List<Conference>) conferenceRepository.findAll());
    }

    public List<SubjectInterest> getTotalSubjectInterest() {
        return Collections.unmodifiableList(conferenceRepository.findTotalInterestInSubjects());
    }
}