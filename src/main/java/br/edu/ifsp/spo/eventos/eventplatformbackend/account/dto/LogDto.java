package br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class LogDto {
    private Instant createdAt;
    private String resourceData;
}
