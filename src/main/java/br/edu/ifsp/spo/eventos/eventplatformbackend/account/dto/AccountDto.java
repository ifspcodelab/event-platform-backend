package br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AccountDto {
    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private Boolean agreed;
}
