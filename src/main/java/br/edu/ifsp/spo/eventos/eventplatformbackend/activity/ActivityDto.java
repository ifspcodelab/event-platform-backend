package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ActivityDto {
    UUID id;
    String title;
    String slug;
    String description;
    ActivityType type;
    EventStatus status;
    ActivityModality modality;
    boolean needRegistration;
    Integer duration;
    Integer setupTime;
    String cancellationMessage;
}
