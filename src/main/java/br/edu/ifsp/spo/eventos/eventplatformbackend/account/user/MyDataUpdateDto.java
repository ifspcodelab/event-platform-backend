package br.edu.ifsp.spo.eventos.eventplatformbackend.account.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyDataUpdateDto {
    private String name;
    private String cpf;
}
