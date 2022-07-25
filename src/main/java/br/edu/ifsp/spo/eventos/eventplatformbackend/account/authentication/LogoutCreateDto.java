package br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
public class LogoutCreateDto {
    @NotNull
    @NotBlank
    String accessToken;
}
