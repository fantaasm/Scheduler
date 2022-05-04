package pl.fantasea.scheduler.exception;

import pl.fantasea.scheduler.model.User;

public class UserLoginEmailMismatchException extends RuntimeException {
    public UserLoginEmailMismatchException(User user) {
        super("Login oraz email nie pasują do siebie. Login: " + user.getLogin() + ", email: " + user.getEmail());
    }
}
