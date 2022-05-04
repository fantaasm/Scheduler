package pl.fantasea.scheduler.exception;

public class ConferenceNotFoundException extends RuntimeException {
    public ConferenceNotFoundException(Long id) {
        super("Nie znaleziono konferencji " + id);
    }
}
