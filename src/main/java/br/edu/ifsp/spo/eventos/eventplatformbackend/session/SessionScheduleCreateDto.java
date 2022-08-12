package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import lombok.Value;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class SessionScheduleCreateDto {
    @NotNull
    LocalDateTime execution_start;
    @NotNull
    LocalDateTime execution_end;
    @URL
    String url;
    UUID locationId;
    UUID areaId;
    UUID spaceId;
}
