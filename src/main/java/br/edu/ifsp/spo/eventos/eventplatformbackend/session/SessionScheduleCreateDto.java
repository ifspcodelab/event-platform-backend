package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.Space;
import lombok.Value;
import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
public class SessionScheduleCreateDto {
    @NotNull
    LocalDateTime execution_start;
    @NotNull
    LocalDateTime execution_end;
    @URL
    String url;
    @Valid
    Location location;
    @Valid
    Area area;
    @Valid
    Space space;
}
