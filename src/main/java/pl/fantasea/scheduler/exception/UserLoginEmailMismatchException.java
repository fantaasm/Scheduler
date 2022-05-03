package pl.fantasea.scheduler.exception;

public class UserLoginEmailMismatchException extends RuntimeException {
    public UserLoginEmailMismatchException(String message) {
        super(message);
    }
}
