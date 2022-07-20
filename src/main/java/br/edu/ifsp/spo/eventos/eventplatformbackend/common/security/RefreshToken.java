package br.edu.ifsp.spo.eventos.eventplatformbackend.common.security;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
public class RefreshToken {
    @Id
    private UUID id;
    private String token;
    @ManyToOne
    private Account account;
}
