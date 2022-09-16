package br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Value
public class AccountDeletionDto {
    @NotNull
    @NotBlank
    String token;
}
