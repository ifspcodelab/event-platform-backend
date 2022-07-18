package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import java.util.UUID;

public class AccountDto {

    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private Boolean agreed;

    public AccountDto(UUID id, String name, String email, String cpf, Boolean agreed) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.agreed = agreed;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public Boolean getAgreed() {
        return agreed;
    }

    public void setAgreed(Boolean agreed) {
        this.agreed = agreed;
    }
}
