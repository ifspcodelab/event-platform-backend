package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.speaker.Speaker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "activities_speakers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ActivitySpeaker {
    @Id
    private UUID id;
    @ManyToOne
    private Activity activity;
    @ManyToOne
    private Speaker speaker;

    public ActivitySpeaker(Activity activity, Speaker speaker) {
        this.id = UUID.randomUUID();
        this.activity = activity;
        this.speaker = speaker;
    }
}
