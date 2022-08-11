package br.edu.ifsp.spo.eventos.eventplatformbackend.account.user;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Name;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class MyDataUpdateDto {
    @NotNull
    @NotBlank
    @Name
    @Size(min = 5, max = 256)
    private String name;
    @NotNull
    @CPF
    private String cpf;
}
