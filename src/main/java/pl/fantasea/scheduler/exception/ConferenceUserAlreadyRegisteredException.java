package pl.fantasea.scheduler.exception;

import pl.fantasea.scheduler.model.User;

public class ConferenceUserAlreadyRegisteredException extends RuntimeException {
    public ConferenceUserAlreadyRegisteredException(User user) {
        super("Podany użytkownik jest już zarejestrowany na tą konferencje: " + user.getLogin());
    }

}
