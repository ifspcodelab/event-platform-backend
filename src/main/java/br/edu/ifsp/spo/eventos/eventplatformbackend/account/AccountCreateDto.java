package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Name;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Password;
import lombok.Value;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.*;

@Value
public class AccountCreateDto {
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
    @NotBlank
    @Size(min = 8, max = 64)
    @Password
    String password;
    @NotNull
    @AssertTrue(message = "Os termos devem estar aceitos")
    Boolean agreed;
    @NotNull
    String userRecaptcha;

    public String getName() {
        return name.strip();
    }

    public String getCpf() {
        return cpf.replace(".","").replace("-","");
    }
}
