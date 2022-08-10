package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.Activity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name= "sessions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Session {
    @Id
    private UUID id;
    private String title;
    private Integer seats;
    private String cancellationMessage;
    @ManyToOne
    private Activity activity;
    @OneToMany(cascade=CascadeType.MERGE)
    private List<SessionSchedule> sessionSchedule;

    public Session(String title, Integer seats, Activity activity, List<SessionSchedule> sessionSchedule ) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.seats = seats;
        this.cancellationMessage = null;
        this.activity = activity;
        this.sessionSchedule = sessionSchedule;
    }
}


