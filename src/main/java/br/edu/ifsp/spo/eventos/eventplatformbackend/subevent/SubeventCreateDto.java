package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import lombok.Value;

import java.time.LocalDate;
@Value
public class SubeventCreateDto {
    String title;
    String slug;
    String summary;
    String presentation;
    LocalDate startDate;
    LocalDate endDate;
    String smallerImage;
    String biggerImage;
}
