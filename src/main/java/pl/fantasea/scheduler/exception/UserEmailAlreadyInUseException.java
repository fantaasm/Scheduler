package pl.fantasea.scheduler.exception;

public class UserEmailAlreadyInUseException extends RuntimeException{
    public UserEmailAlreadyInUseException(String message) {
        super(message);
    }
}
