package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;


import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Value
public class PasswordResetDto {

    UUID token;

    @NotNull
    @NotBlank
    @Size(min = 8, max = 64)
    String password;
}
