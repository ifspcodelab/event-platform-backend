package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;


import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Password;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.util.UUID;

@Value
public class PasswordResetDto {

    @NotNull
    String token;

    @NotNull
    @Size(min = 8, max = 64)
    @Password
    String password;

    @NotNull
    String userRecaptcha;

}
