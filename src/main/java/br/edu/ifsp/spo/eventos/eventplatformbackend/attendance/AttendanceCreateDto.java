package br.edu.ifsp.spo.eventos.eventplatformbackend.attendance;

import lombok.Data;

import java.util.UUID;

@Data
public class AttendanceCreateDto {
    UUID registrationId;
}
