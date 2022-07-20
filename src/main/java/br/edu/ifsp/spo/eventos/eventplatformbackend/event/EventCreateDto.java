package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.Period;
import lombok.Value;
import org.hibernate.validator.constraints.URL;
import javax.validation.Valid;
import javax.validation.constraints.*;

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
    @NotNull
    @Valid
    Period registrationPeriod;
    @NotNull
    @Valid
    Period executionPeriod;
    @NotNull
    @URL
    String smallerImage;
    @NotNull
    @URL
    String biggerImage;
}
