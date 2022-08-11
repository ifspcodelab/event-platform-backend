package br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class AccountManagementDto extends AccountDto {
    private String role;
    private Boolean verified;
    private Instant registrationTimestamp;
}
