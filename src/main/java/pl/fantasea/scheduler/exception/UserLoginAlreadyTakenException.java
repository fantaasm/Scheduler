package pl.fantasea.scheduler.exception;

public class UserLoginAlreadyTakenException extends RuntimeException {
    public UserLoginAlreadyTakenException() {
        super("Użytkownik z takim loginem lub adresem email już istnieje");
    }
}
