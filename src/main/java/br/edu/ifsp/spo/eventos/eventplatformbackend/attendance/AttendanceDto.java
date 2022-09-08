package br.edu.ifsp.spo.eventos.eventplatformbackend.attendance;

import br.edu.ifsp.spo.eventos.eventplatformbackend.registration.RegistrationDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionScheduleDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class AttendanceDto {
    UUID id;
    RegistrationDto registration;
    SessionScheduleDto sessionSchedule;
}
