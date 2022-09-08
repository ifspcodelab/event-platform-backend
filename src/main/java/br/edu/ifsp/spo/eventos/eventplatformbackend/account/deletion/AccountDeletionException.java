package br.edu.ifsp.spo.eventos.eventplatformbackend.account.deletion;

import lombok.Getter;

@Getter
public class AccountDeletionException extends RuntimeException{
    private AccountDeletionExceptionType type;
    private String email;


    public AccountDeletionException(AccountDeletionExceptionType type, String email){
        this.type = type;
        this.email = email;
    }
}
