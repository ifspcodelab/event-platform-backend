package br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class AccountDto {
    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private Boolean agreed;
    private String role;
    private Boolean verified;
    private Instant registrationTimestamp;
}