package pl.fantasea.scheduler.exception;

import pl.fantasea.scheduler.model.User;

public class ConferenceUserNotRegisteredException extends RuntimeException {
    public ConferenceUserNotRegisteredException(User user) {
        super("Użytkownik " + user.getLogin() + " nie jest zarejestrowany na tą konferencji");
    }
}
