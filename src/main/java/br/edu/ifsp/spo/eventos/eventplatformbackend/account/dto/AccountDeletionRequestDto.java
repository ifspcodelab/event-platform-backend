package br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Password;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
public class AccountDeletionRequestDto {
    @NotNull
    @NotBlank
    @Size(min = 8, max = 64)
    @Password
    String password;
    @NotNull
    String userRecaptcha;
}
