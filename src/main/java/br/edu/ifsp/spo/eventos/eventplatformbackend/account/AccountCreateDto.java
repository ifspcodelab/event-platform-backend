package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import lombok.Value;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
public class AccountCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 5, max = 256)
    String name;
    @NotNull
    @Email
    String email;
    @NotNull
    @CPF
    String cpf;
    @NotNull
    @NotBlank
    @Size(min = 8, max = 64)
    String password;
    @NotNull
    Boolean agreed;
}