package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import lombok.Value;

import java.time.LocalDate;

@Value
public class EventCreateDto {
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
}
