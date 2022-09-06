package br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSiteDto {
    UUID id;
    String title;
    String slug;
    String summary;
    String presentation;
    String contact;
    Period registrationPeriod;
    Period executionPeriod;
    String smallerImage;
    String biggerImage;
    EventStatus status;
    String cancellationMessage;
}
