package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountDto;
import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
public class OrganizerCreateDto {
    @Valid
    @NotNull
    OrganizerType type;
    @Valid
    @NotBlank
    @NotNull
    AccountDto account;
}
