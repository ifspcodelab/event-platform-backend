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
    @Min(value = 0)
    Integer seats;
    @NotNull
    @Valid
    @Size(min = 1)
    List<SessionScheduleCreateDto> sessionSchedules;

    public String getTitle() {
        return title.strip();
    }
}
