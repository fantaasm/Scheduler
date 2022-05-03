package pl.fantasea.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.fantasea.scheduler.exception.ConferenceNotFoundException;
import pl.fantasea.scheduler.exception.ConferenceUserAlreadyRegisteredException;
import pl.fantasea.scheduler.exception.ConferenceUserLimitExceededException;
import pl.fantasea.scheduler.exception.UserLoginAlreadyTakenException;
import pl.fantasea.scheduler.model.Conference;
import pl.fantasea.scheduler.model.User;
import pl.fantasea.scheduler.repository.ConferenceRepository;
import pl.fantasea.scheduler.repository.UserRepository;
import pl.fantasea.scheduler.service.ConferenceService;

import java.time.LocalDateTime;
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
        var user = new User().setLogin("testUser").setEmail("test@test.pl");
        var result = assertDoesNotThrow(() -> conferenceService.registerUser(user.getEmail(), user.getLogin(), anyLong()));
        assertThat(result).isTrue();

        //verify
        verify(conferenceRepository).findById(anyLong());
    }

    @Test
    public void registerUser_should_throw_exception_if_login_taken() throws UserLoginAlreadyTakenException {
        // arrange
        var exampleConference = createRandomConference();
        var user = new User().setLogin("testUser").setEmail("test@test.pl");
        given(userRepository.findByLogin(anyString())).willReturn(Optional.of(user));
        given(conferenceRepository.findById(anyLong())).willReturn(Optional.of(exampleConference));

        // act & assert
        var newUser = new User().setLogin("testUser").setEmail("test1@test.pl");
        assertThrows(UserLoginAlreadyTakenException.class, () -> conferenceService.registerUser(newUser.getEmail(), newUser.getLogin(), anyLong()));

        //verify
        verify(conferenceRepository).findById(anyLong());
        verify(userRepository).findByLogin(anyString());
    }

    @Test
    public void registerUser_should_throw_exception_if_conference_doesnt_exists() throws ConferenceNotFoundException {
        // arrange
        // act & assert
        var user = new User().setLogin("test").setEmail("test@test.pl");
        assertThrows(ConferenceNotFoundException.class, () -> conferenceService.registerUser(user.getEmail(), user.getLogin(), anyLong()));
        //verify
    }

    @Test
    public void registerUser_should_throw_exception_if_conference_exceeds_max_participants() throws ConferenceUserLimitExceededException {
        // arrange
        var exampleConference = createRandomConference();

        for (int i = 0; i < exampleConference.getMaxParticipants(); i++) {
            var user = new User().setLogin("test" + i).setEmail("test" + i + "@test.pl").setConferences(Set.of(exampleConference));
            lenient().when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));
            exampleConference.getRegisteredUsers().add(user);
        }
        given(conferenceRepository.findById(anyLong())).willReturn(Optional.of(exampleConference));

        // act & assert
        var user = new User().setLogin("test6").setEmail("test6@test.pl");
        assertThrows(ConferenceUserLimitExceededException.class, () -> conferenceService.registerUser(user.getEmail(), user.getLogin(), anyLong()));

        //verify
        verify(conferenceRepository).findById(anyLong());
    }

    @Test
    public void registerUser_should_throw_exception_if_already_registered() throws ConferenceUserAlreadyRegisteredException {

        // arrange
        var exampleConference = createRandomConference();
        var user = new User().setLogin("test1").setEmail("test1@test.pl").setConferences(Set.of(exampleConference));

        given(conferenceRepository.findById(anyLong())).willReturn(Optional.of(exampleConference.setRegisteredUsers(Set.of(user))));
        given(userRepository.findByLogin("test1")).willReturn(Optional.of(user));

        // act & assert
        var newUser = new User().setLogin("test1").setEmail("test1@test.pl");
        assertThrows(ConferenceUserAlreadyRegisteredException.class, () -> conferenceService.registerUser(newUser.getEmail(), newUser.getLogin(), anyLong()));

        //verify
        verify(conferenceRepository).findById(anyLong());
        verify(userRepository).findByLogin("test1");
    }

    @Test
    public void sendEmail_should_return_true_if_email_sent() {
        // arrange
        // act & assert
        var message = "Test message";
        var result = conferenceService.sendEmail(message);
        assertThat(result).isTrue();
        //verify
    }


}