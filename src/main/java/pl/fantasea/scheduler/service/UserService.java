package pl.fantasea.scheduler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.fantasea.scheduler.dto.UserDto;
import pl.fantasea.scheduler.exception.UserEmailAlreadyInUseException;
import pl.fantasea.scheduler.exception.UserLoginEmailMismatchException;
import pl.fantasea.scheduler.model.User;
import pl.fantasea.scheduler.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean updateEmail(User user, String newEmail) {
        if (user.getEmail().equals(newEmail))
            throw new IllegalArgumentException("Nowy email jest taki sam jak poprzedni");

        var dbUser = userRepository.findByLoginAndEmail(user.getLogin(), user.getEmail())
                                         .orElseThrow(() -> new UserLoginEmailMismatchException(user));

        var userWithNewEmail = userRepository.findByEmail(newEmail);

        if (userWithNewEmail.isPresent())
            throw new UserEmailAlreadyInUseException(newEmail);

        dbUser.setEmail(newEmail);
        userRepository.save(dbUser);
        return true;
    }

    public List<UserDto> getAllUsers() {
        return Collections.unmodifiableList(userRepository.getAllUsersWithEmail());
    }
}