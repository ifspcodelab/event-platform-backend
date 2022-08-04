package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;
import lombok.Data;
import java.util.UUID;

@Data
public class SubeventDto {
    UUID id;
    String title;
    String slug;
    String summary;
    String presentation;
    Period executionPeriod;
    String smallerImage;
    String biggerImage;
    EventStatus status;
    String cancellationMessage;
}
