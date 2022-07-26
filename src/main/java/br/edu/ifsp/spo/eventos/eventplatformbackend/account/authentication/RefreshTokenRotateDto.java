package br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RefreshTokenRotateDto {
    @NotNull
    @NotBlank
    String refreshToken;
}
