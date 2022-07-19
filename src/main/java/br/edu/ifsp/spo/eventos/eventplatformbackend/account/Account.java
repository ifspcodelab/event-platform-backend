package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account {
    private UUID id;
    private String email;
    private String password;
    private String role;

    public Account(String email, String password, String role) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
