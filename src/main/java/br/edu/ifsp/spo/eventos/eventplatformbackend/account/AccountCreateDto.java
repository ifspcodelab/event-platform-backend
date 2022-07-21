package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import lombok.Value;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.*;

@Value
public class AccountCreateDto {
    @NotNull
    @NotBlank
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
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$", flags = Pattern.Flag.UNICODE_CASE, message = "A senha deve ter uma maiúscula, uma minúscula, um número e um caractere especial")
    String password;
    @NotNull
    @AssertTrue(message = "Os termos devem estar aceitos")
    Boolean agreed;
}
