package br.edu.ifsp.spo.eventos.eventplatformbackend.attendance;

import br.edu.ifsp.spo.eventos.eventplatformbackend.registration.Registration;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionSchedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendances")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Attendance {
    @Id
    private UUID id;
    private LocalDateTime createdAt;
    @ManyToOne
    private Registration registration;
    @ManyToOne
    private SessionSchedule sessionSchedule;

    public Attendance(Registration registration, SessionSchedule sessionSchedule) {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.registration = registration;
        this.sessionSchedule = sessionSchedule;
    }

    public String toLog() {
        return "Attendance{" +
            "id=" + id +
            ", createdAt=" + createdAt +
            ", registration=" + registration.toLog() +
            ", sessionSchedule=" + sessionSchedule.toLog() +
            '}';
    }
}


