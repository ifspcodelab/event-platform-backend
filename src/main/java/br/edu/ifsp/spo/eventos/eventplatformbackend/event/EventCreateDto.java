package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import lombok.Data;
import lombok.Value;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Value
public class EventCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 3, max = 50)
    String title;
    @NotNull
    @NotBlank
    @Size(min = 1)
    String slug;
    @NotNull
    @NotBlank
    @Size(min = 100, max = 150)
    String summary;
    @NotNull
    @NotBlank
    @Size(min = 1000, max = 5000)
    String presentation;

    // TODO: validar datas de registro e início do evento
    @NotNull
    LocalDate registrationStartDate;
    @NotNull
    LocalDate registrationEndDate;
    @NotNull
    LocalDate startDate;
    @NotNull
    LocalDate endDate;
    @NotNull
    @URL
    String smallerImage;
    @NotNull
    @URL
    String biggerImage;
}
