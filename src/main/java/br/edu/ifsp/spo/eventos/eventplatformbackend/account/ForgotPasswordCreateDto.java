package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import lombok.Data;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class ForgotPasswordCreateDto {
    @NotNull
    @Email
    String email;
}

