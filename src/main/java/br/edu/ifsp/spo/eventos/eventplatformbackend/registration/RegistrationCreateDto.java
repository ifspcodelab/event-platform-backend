package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCreateDto {
    UUID accountId;
}
