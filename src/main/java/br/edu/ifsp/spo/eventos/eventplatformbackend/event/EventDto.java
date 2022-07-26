package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;
import lombok.Data;
import java.util.UUID;

@Data
public class EventDto {
    UUID id;
    String title;
    String slug;
    String summary;
    String presentation;
    Period registrationPeriod;
    Period executionPeriod;
    String smallerImage;
    String biggerImage;
    EventStatus status;
}
