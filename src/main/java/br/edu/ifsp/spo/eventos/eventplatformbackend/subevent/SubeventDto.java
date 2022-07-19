package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class SubeventDto {
    private UUID id;
    private String title;
    private String slug;
    private String summary;
    private String presentation;
    private LocalDate startDate;
    private LocalDate endDate;
    private String smallerImage;
    private String biggerImage;
    private EventStatus status;

}
