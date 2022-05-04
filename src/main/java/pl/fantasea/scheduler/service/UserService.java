package pl.fantasea.scheduler.service;

import org.springframework.stereotype.Service;
import pl.fantasea.scheduler.exception.UserEmailAlreadyInUseException;
import pl.fantasea.scheduler.exception.UserLoginEmailMismatchException;
import pl.fantasea.scheduler.model.User;
import pl.fantasea.scheduler.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean updateEmail(User user, String newEmail) {

        if (user.getEmail().equals(newEmail)) {
            throw new IllegalArgumentException("New email is the same as old one");
        }

        var dbUser = userRepository.findByLoginAndEmail(user.getLogin(), user.getEmail()).orElseThrow(() -> new UserLoginEmailMismatchException(
                "User with login: " + user.getLogin() + " and email: " + user.getEmail() + " does not exist"));

        var userWithNewEmail = userRepository.findByEmail(newEmail);

        if (userWithNewEmail.isPresent()) {
            throw new UserEmailAlreadyInUseException("User with email: " + newEmail + " already exists");
        }

        dbUser.setEmail(newEmail);
        userRepository.save(dbUser);
        return true;
    }

    public List<User> getAllUsers() {
        return Collections.unmodifiableList((List<User>) userRepository.findAll());
    }

}
