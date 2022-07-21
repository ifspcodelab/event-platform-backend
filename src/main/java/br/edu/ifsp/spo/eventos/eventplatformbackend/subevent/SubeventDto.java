package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.Period;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
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
}
