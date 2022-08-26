package br.edu.ifsp.spo.eventos.eventplatformbackend.account.deletion;

import lombok.Getter;

@Getter
public class AccountDeletionException extends RuntimeException{
    private String email;

    public AccountDeletionException(String email){
        this.email = email;
    }
}
