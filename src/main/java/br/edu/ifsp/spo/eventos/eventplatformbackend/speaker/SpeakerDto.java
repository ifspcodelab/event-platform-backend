package br.edu.ifsp.spo.eventos.eventplatformbackend.speaker;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.AccountDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SpeakerDto {
    UUID id;
    String name;
    String email;
    String cpf;
    String curriculum;
    String lattes;
    String linkedin;
    String phoneNumber;
    AccountDto account;
}
