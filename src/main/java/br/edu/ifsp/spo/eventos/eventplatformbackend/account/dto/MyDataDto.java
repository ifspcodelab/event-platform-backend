package br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyDataDto {
    private String name;
    private String email;
    private String cpf;
}
