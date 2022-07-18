package br.edu.ifsp.spo.eventos.eventplatformbackend.account;


import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
public class ResetPasswordCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 20, max = 200)
    String token;

    @NotNull
    @NotBlank
    @Size(min = 8, max = 64)
    String newPassword;
}
