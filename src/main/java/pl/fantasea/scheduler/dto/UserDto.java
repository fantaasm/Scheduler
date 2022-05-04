package pl.fantasea.scheduler.dto;

import lombok.*;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@ToString
@RequiredArgsConstructor
public class UserDto {

    @NotBlank(message = "Login nie może być pusty")
    private final String login;

    @Email(message = "Email jest niewłaściwy")
    @NotBlank(message = "Email nie może być pusty")
    private final String email;

}

