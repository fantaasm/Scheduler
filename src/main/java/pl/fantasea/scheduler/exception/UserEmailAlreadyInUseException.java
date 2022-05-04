package pl.fantasea.scheduler.exception;

public class UserEmailAlreadyInUseException extends RuntimeException {
    public UserEmailAlreadyInUseException(String email) {
        super("Użytkownik o adresie email: " + email + " już istnieje");
    }
}
