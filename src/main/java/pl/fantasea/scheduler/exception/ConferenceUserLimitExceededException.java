package pl.fantasea.scheduler.exception;

public class ConferenceUserLimitExceededException extends RuntimeException {
    public ConferenceUserLimitExceededException(String message) {
        super(message);
    }
}
