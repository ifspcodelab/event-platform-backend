package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.Session;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
public class RegistrationDto {
    UUID id;
    LocalDateTime date;
    Account account;
    Session session;
    Event event;
    RegistrationStatus registrationStatus;
    LocalDateTime timeEmailWasSent;
}
