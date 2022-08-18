package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import lombok.Value;
import java.util.UUID;

@Value
public class RegistrationCreateDto {
    UUID userId;
}
