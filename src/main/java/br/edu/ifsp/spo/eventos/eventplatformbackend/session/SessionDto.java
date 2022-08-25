package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SessionDto {
    UUID id;
    String title;
    Integer seats;
    String cancellationMessage;
    boolean canceled;
    ActivityDto activity;
    List<SessionScheduleDto> sessionsSchedule;
}
