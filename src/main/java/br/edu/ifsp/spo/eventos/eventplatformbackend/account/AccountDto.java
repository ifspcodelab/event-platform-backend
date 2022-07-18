package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AccountDto {
    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private Boolean agreed;
}
