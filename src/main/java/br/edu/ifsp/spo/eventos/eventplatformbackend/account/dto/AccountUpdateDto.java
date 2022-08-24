package br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Name;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Password;
import lombok.Value;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.*;

@Value
public class AccountUpdateDto {
    @NotNull
    @NotBlank
    @Name
    @Size(min = 5, max = 256)
    String name;
    @NotNull
    @Email
    @Size(max = 350)
    String email;
    @NotNull
    @CPF
    String cpf;
    @NotNull
    Boolean verified;
    @NotNull
    @NotBlank
    String role;

    public String getName() {
        return name.strip();
    }

    public String getCpf() {
        return cpf.replace(".","").replace("-","");
    }
}
