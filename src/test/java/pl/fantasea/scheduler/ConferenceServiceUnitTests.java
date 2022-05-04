package pl.fantasea.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import pl.fantasea.scheduler.exception.*;
import pl.fantasea.scheduler.model.Conference;
import pl.fantasea.scheduler.model.User;
import pl.fantasea.scheduler.repository.ConferenceRepository;
import pl.fantasea.scheduler.repository.UserRepository;
import pl.fantasea.scheduler.service.ConferenceService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ConferenceServiceUnitTests {
    private ConferenceService conferenceService;
    @Mock
    private ConferenceRepository conferenceRepository;
    @Mock
    private UserRepository userRepository;

    private static Conference createRandomConference() {
        var random = new Random();
        return new Conference()
                .setName("Example Conference - " + random.nextInt(100))
                .setMaxParticipants(5)
                .setStartDate(LocalDateTime.now())
                .setEndDate(LocalDateTime.now().plusHours(random.nextInt(5)));
    }

    @BeforeEach
    public void setUp() {
        conferenceService = new ConferenceService(conferenceRepository, userRepository);
    }

    @Test
    public void registerUser_should_return_true_if_success() {
        // arrange
        var exampleConference = createRandomConference();

        given(conferenceRepository.findById(anyLong())).willReturn(Optional.of(exampleConference));

        // act & assert
        var user = new User("testUser", "test@test.pl");
        var result = assertDoesNotThrow(() -> conferenceService.registerUser(user, anyLong()));
        assertThat(result).isTrue();

        // verify
        verify(conferenceRepository).findById(anyLong());
    }

    @Test
    public void registerUser_should_throw_exception_if_login_taken() throws UserLoginAlreadyTakenException {
        // arrange
        var exampleConference = createRandomConference();
        var user = new User("testUser", "test1@test.pl");
        given(userRepository.findByLoginOrEmail(user.getLogin(), "test1@test.pl")).willReturn(Optional.of(user));
        given(userRepository.findByLoginOrEmail(user.getLogin(), "test2@test.pl")).willReturn(Optional.of(user));
        given(conferenceRepository.findById(anyLong())).willReturn(Optional.of(exampleConference));

        // act & assert
        var newUser = new User("testUser", "test2@test.pl");
        assertThrows(UserLoginAlreadyTakenException.class, () -> conferenceService.registerUser(newUser, anyLong()));

        // verify
        verify(conferenceRepository).findById(anyLong());
        verify(userRepository).findByLoginOrEmail(newUser.getLogin(), newUser.getEmail());
    }

    @Test
    public void registerUser_should_throw_exception_if_conference_doesnt_exists() throws ConferenceNotFoundException {
        // arrange
        // act & assert
        var user = new User("test", "test@test.pl");
        assertThrows(ConferenceNotFoundException.class, () -> conferenceService.registerUser(user, anyLong()));
        // verify
    }

    @Test
    public void registerUser_should_throw_exception_if_conference_exceeds_max_participants() throws ConferenceUserLimitExceededException {
        // arrange
        var exampleConference = createRandomConference();

        for (int i = 0; i < exampleConference.getMaxParticipants(); i++) {
            var user = new User("test" + i, "test" + i + "@test.pl").setConferences(Set.of(exampleConference));
            lenient().when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));
            exampleConference.getRegisteredUsers().add(user);
        }
        given(conferenceRepository.findById(anyLong())).willReturn(Optional.of(exampleConference));

        // act & assert
        var user = new User("test6", "test6@test.pl");
        assertThrows(ConferenceUserLimitExceededException.class, () -> conferenceService.registerUser(user, anyLong()));

        // verify
        verify(conferenceRepository).findById(anyLong());
    }

    @Test
    public void registerUser_should_throw_exception_if_already_registered() throws ConferenceUserAlreadyRegisteredException {
        // arrange
        var exampleConference = createRandomConference();
        var user = new User("test1", "test1@test.pl").setConferences(Set.of(exampleConference));
        given(conferenceRepository.findById(anyLong())).willReturn(Optional.of(exampleConference.setRegisteredUsers(Set.of(user))));
        given(userRepository.findByLoginOrEmail(user.getLogin(), user.getEmail())).willReturn(Optional.of(user));

        // act & assert
        var newUser = new User("test1", "test1@test.pl");
        assertThrows(ConferenceUserAlreadyRegisteredException.class, () -> conferenceService.registerUser(newUser, anyLong()));

        // verify
        verify(conferenceRepository).findById(anyLong());
        verify(userRepository).findByLoginOrEmail(user.getLogin(), user.getEmail());
    }

    @Test
    public void unregisterUser_should_return_true_if_unregistered() {
        // arrange
        var exampleConference = createRandomConference();
        exampleConference.setId(1L);
        var exampleUser = new User("test1", "test1@test.pl").setConferences(new HashSet<>(Set.of(exampleConference))); // because Set.of returns immutable set and DB not
        given(conferenceRepository.findById(anyLong())).willReturn(Optional.of(exampleConference.setRegisteredUsers(Set.of(exampleUser))));
        given(userRepository.findByLoginAndEmail(exampleUser.getLogin(), exampleUser.getEmail())).willReturn(Optional.of(exampleUser));

        // act & assert
        var result = conferenceService.unregisterUser(exampleUser, 1L);
        assertThat(result).isTrue();

        // verify
        verify(conferenceRepository).findById(anyLong());
        verify(userRepository).findByLoginAndEmail(anyString(), anyString());
    }

    @Test
    public void unregisterUser_should_throw_exception_if_login_email_mismatch() {
        // arrange
        var exampleConference = createRandomConference();
        var exampleUser = new User("test1", "test1@test.pl").setConferences(Set.of(exampleConference));
        given(conferenceRepository.findById(anyLong())).willReturn(Optional.of(exampleConference.setRegisteredUsers(Set.of(exampleUser))));
        lenient().when(userRepository.findByLoginAndEmail(exampleUser.getLogin(), exampleUser.getEmail())).thenReturn(Optional.of(exampleUser));

        // act & assert
        var mismatchedEmailUser = new User("test1", "test12@test.pl");
        assertThrows(UserLoginEmailMismatchException.class, () -> conferenceService.unregisterUser(mismatchedEmailUser, 1L));

        // verify
        verify(userRepository).findByLoginAndEmail(mismatchedEmailUser.getLogin(), mismatchedEmailUser.getEmail());
    }

    @Test
    public void unregisterUser_should_throw_exception_if_user_not_registered() {
        // arrange
        var exampleConference = createRandomConference();
        var exampleUser = new User("test1", "test1@test.pl");
        given(conferenceRepository.findById(anyLong())).willReturn(Optional.of(exampleConference));
        given(userRepository.findByLoginAndEmail("test1", "test1@test.pl")).willReturn(Optional.of(exampleUser));

        // act & assert
        assertThrows(ConferenceUserNotRegisteredException.class, () -> conferenceService.unregisterUser(exampleUser, 1L));

        // verify
        verify(conferenceRepository).findById(anyLong());
        verify(userRepository).findByLoginAndEmail("test1", "test1@test.pl");
    }

    @Test
    public void sendEmail_should_return_true_if_email_sent() {
        // arrange
        var exampleUser = new User("test1", "test1@test.pl");

        // act & assert
        var message = "Test message";
        var result = conferenceService.sendEmail(exampleUser, message);
        assertThat(result).isTrue();
        // verify
    }


}