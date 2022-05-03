package pl.fantasea.scheduler.exception;

public class UserLoginAlreadyTakenException extends RuntimeException{
    public UserLoginAlreadyTakenException() {
        super("User login already taken");
    }
}
