package pl.fantasea.scheduler.dto;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Getter
@ToString
public class UserUpdateMailDto extends UserDto {

    @Email(message = "Email jest niewłaściwy")
    @NotBlank(message = "Email nie może być pusty")
    private final String newEmail;


    public UserUpdateMailDto(@NotBlank(message = "Login nie może być pusty") String login,
                             @Email(message = "Email jest niewłaściwy") @NotBlank(message = "Email nie może być pusty") String email,
                             String newEmail) {
        super(login, email);
        this.newEmail = newEmail;
    }
}

