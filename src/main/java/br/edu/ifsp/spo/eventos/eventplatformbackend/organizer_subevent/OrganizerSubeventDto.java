package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class OrganizerSubeventDto {
    private UUID id;
    private OrganizerSubeventType type;
    private AccountDto account;
}
