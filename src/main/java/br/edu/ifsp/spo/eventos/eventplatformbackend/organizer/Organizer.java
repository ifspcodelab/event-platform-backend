package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "organizers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Organizer {
    @Id
    private UUID id;
    @Enumerated(EnumType.STRING)
    private OrganizerType type;
    @ManyToOne
    private Account account;
    @ManyToOne
    private Event event;

    public Organizer(OrganizerType type, Account account, Event event) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.account = account;
        this.event = event;
    }
}
