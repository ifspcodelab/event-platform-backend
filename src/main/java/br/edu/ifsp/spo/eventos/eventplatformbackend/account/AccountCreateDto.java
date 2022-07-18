package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AccountCreateDto {

    @NotNull
    @NotBlank
    @Size(min = 5, max = 256)
    private String name;
    @NotNull
    @Email
    private String email;
    @NotNull
    @CPF
    private String cpf;
    @NotNull
    @NotBlank
    @Size(min = 8, max = 64)
    private String password;
    @NotNull
    private Boolean agreed;

    public AccountCreateDto(String name, String email, String cpf, String password, Boolean agreed) {
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.password = password;
        this.agreed = agreed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAgreed() {
        return agreed;
    }

    public void setAgreed(Boolean agreed) {
        this.agreed = agreed;
    }
}
