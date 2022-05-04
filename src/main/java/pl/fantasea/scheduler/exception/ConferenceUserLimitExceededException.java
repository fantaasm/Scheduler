package pl.fantasea.scheduler.exception;

public class ConferenceUserLimitExceededException extends RuntimeException {
    public ConferenceUserLimitExceededException(int amount) {
        super("Konferencja przekroczyła maksymalną ilość uczestników: " + amount);
    }
}
