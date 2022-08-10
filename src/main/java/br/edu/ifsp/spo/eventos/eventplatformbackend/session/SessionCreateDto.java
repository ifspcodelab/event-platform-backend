package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@Value
public class SessionCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 1, max= 30)
    String title;
    @NotNull
    @Min(value = 1)
    Integer seats;
    @NotNull
    @Valid
    List<SessionSchedule> sessionSchedule;
}
