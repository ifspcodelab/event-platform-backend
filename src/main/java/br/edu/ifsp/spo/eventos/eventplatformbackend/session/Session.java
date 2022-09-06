package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.Activity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name= "sessions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Session implements Diffable<Session> {
    @Id
    private UUID id;
    private String title;
    private Integer seats;
    private String cancellationMessage;
    private boolean canceled;
    @ManyToOne
    private Activity activity;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "session_id", nullable = false, insertable = false, updatable = false)
    private List<SessionSchedule> sessionSchedules;

    public Session(String title, Integer seats, Activity activity, List<SessionSchedule> sessionSchedules) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.seats = seats;
        this.cancellationMessage = null;
        this.activity = activity;
        this.sessionSchedules = sessionSchedules;
        this.sessionSchedules.forEach(sessionSchedule -> sessionSchedule.setSession(this));
    }

    @Override
    public DiffResult<Session> diff(Session updatedSession) {
        return new DiffBuilder<>(this, updatedSession, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Título", this.title, updatedSession.title)
                .append("Número de vagas", this.seats, updatedSession.seats)
                .append("Horários da sessão", this.sessionSchedules, updatedSession.sessionSchedules)
                .build();
    }
}
