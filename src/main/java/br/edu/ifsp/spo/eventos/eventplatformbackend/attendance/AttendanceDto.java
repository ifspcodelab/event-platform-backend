package br.edu.ifsp.spo.eventos.eventplatformbackend.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Data
public class AttendanceDto {
    UUID id;
    Instant registrationTimestamp;
    UUID sessionId;
    UUID sessionScheduleId;
}
