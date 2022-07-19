package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class EventDto {
    UUID id;
    String title;
    String slug;
    String summary;
    String presentation;
    LocalDate registrationStartDate;
    LocalDate registrationEndDate;
    LocalDate startDate;
    LocalDate endDate;
    String smallerImage;
    String biggerImage;
    EventStatus status;
}
