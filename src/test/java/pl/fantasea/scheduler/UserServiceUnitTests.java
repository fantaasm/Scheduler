package pl.fantasea.scheduler;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pl.fantasea.scheduler.exception.UserEmailAlreadyInUseException;
import pl.fantasea.scheduler.exception.UserLoginEmailMismatchException;
import pl.fantasea.scheduler.model.User;
import pl.fantasea.scheduler.repository.UserRepository;
import pl.fantasea.scheduler.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceUnitTests {

    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    public void updateEmail_should_return_true_if_email_is_updated() {
        // arrange
        var exampleUser = new User().setLogin("test").setEmail("test@test.pl");
       when(userRepository.findByLoginAndEmail(exampleUser.getLogin(), exampleUser.getEmail())).thenReturn(Optional.of(exampleUser));

        // act & assert
        var result = userService.updateEmail(exampleUser, "test2@test.pl");
        assertThat(result).isTrue();

        // verify
        verify(userRepository).findByLoginAndEmail(exampleUser.getLogin(), "test@test.pl");
    }

    @Test
    public void updateEmail_should_throw_exception_if_newEmail_is_same_as_oldEmail() {
        // arrange
        var exampleUser = new User().setLogin("test").setEmail("test@test.pl");
        var exampleUserSameMail = new User().setLogin("test").setEmail("test@test.pl");

        // act & assert
        assertThrows(IllegalArgumentException.class, () -> userService.updateEmail(exampleUser, exampleUserSameMail.getEmail()));

        // verify
    }

    @Test
    public void updateEmail_should_throw_exception_if_newEmail_is_already_in_use() {
        // arrange
        var exampleUser1 = new User().setLogin("test").setEmail("test@test.pl");
        var exampleUser2 = new User().setLogin("test2").setEmail("test2@test.pl");
        given(userRepository.findByLoginAndEmail(exampleUser1.getLogin(), exampleUser1.getEmail())).willReturn(Optional.of(exampleUser1));
        given(userRepository.findByEmail( exampleUser2.getEmail())).willReturn(Optional.of(exampleUser2));

        // act & assert
        assertThrows(UserEmailAlreadyInUseException.class, () -> userService.updateEmail(exampleUser1, exampleUser2.getEmail()));

        // verify
        verify(userRepository).findByLoginAndEmail(exampleUser1.getLogin(), exampleUser1.getEmail());
    }

    @Test
    public void updateEmail_should_throw_exception_if_email_and_login_dont_match() {
        // arrange
        var exampleUser = new User().setLogin("test").setEmail("test@test.pl");
        given(userRepository.findByLoginAndEmail(exampleUser.getLogin(), exampleUser.getEmail())).willReturn(Optional.of(exampleUser));

        // act & assert
        var incomingUserWithWrongLogin = new User().setLogin("test2").setEmail("test@test.pl");
        assertThrows(UserLoginEmailMismatchException.class, () -> userService.updateEmail(incomingUserWithWrongLogin, "newEmail@test.pl"));

        // verify
        verify(userRepository).findByLoginAndEmail(incomingUserWithWrongLogin.getLogin(), incomingUserWithWrongLogin.getEmail());
    }


}