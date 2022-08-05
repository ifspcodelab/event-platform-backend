package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class OrganizerDto {
    UUID id;
    OrganizerType type;
    AccountDto account;
}
