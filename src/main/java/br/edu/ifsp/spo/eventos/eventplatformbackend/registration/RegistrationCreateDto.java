package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class RegistrationCreateDto {
    UUID accountId;
}
