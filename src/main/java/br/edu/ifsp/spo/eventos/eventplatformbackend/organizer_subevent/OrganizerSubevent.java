package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "organizers_subevent")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrganizerSubevent {
    @Id
    private UUID id;
    @Enumerated(EnumType.STRING)
    private OrganizerSubeventType type;
    @ManyToOne
    private Account account;
    @ManyToOne
    private Event event;
    @ManyToOne
    private Subevent subevent;

    public OrganizerSubevent(OrganizerSubeventType type, Account account, Event event, Subevent subevent) {
        this.id = UUID.randomUUID();
        this.account = account;
        this.type = type;
        this.event = event;
        this.subevent = subevent;
    }
}
