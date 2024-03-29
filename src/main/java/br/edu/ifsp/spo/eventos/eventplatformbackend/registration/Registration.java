package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.Session;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registrations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Registration {
    @Id
    private UUID id;
    private LocalDateTime date;
    @ManyToOne
    private Account account;
    @ManyToOne
    private Session session;
    @Enumerated(EnumType.STRING)
    private RegistrationStatus registrationStatus;
    private LocalDateTime timeEmailWasSent;
    private LocalDateTime emailReplyDate;

    public Registration(
        Account account,
        Session session,
        RegistrationStatus registrationStatus
    ) {
        this.id = UUID.randomUUID();
        this.date = LocalDateTime.now();
        this.account = account;
        this.session = session;
        this.registrationStatus = registrationStatus;
    }

    public static Registration createWithConfirmedStatus(Account account, Session session) {
        return new Registration(account, session, RegistrationStatus.CONFIRMED);
    }

    public static Registration createWithWaitingListdStatus(Account account, Session session) {
        return new Registration(account, session, RegistrationStatus.WAITING_LIST);
    }

    public boolean canBeCanceled() {
        return
            this.registrationStatus == RegistrationStatus.CONFIRMED ||
            this.registrationStatus == RegistrationStatus.WAITING_LIST ||
            this.registrationStatus == RegistrationStatus.WAITING_CONFIRMATION;
    }

    public String toLog() {
        return "Registration{" +
            "id=" + id +
            ", account=" + account.toLog() +
            ", session=" + session.toLog() +
            '}';
    }
}
