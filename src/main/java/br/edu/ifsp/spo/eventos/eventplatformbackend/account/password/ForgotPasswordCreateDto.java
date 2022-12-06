package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;

import lombok.Data;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Value
public class ForgotPasswordCreateDto {
    @NotNull
    @Email
    String email;

    @NotNull
    String userRecaptcha;
}

