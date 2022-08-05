package br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
public class LoginCreateDto {
    @NotNull
    @Email
    String email;
    @NotNull
    @NotBlank
    @Size(min = 8, max = 64)
    String password;
    @NotNull
    String recaptcha;
}
