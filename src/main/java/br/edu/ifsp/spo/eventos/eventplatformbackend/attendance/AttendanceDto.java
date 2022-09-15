package br.edu.ifsp.spo.eventos.eventplatformbackend.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
public class AttendanceDto {
    UUID id;
    LocalDateTime createdAt;
    UUID sessionId;
    UUID sessionScheduleId;
    UUID registrationId;
}
