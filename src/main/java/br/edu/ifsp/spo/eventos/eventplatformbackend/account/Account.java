package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Account {
    @Id
    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private String password;
    private Boolean agreed;
    @Enumerated(EnumType.STRING)
    private AccountRole role;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private Instant registrationTimestamp;

    public Account(String name, String email, String cpf, String password, Boolean agreed) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.password = password;
        this.agreed = agreed;
        this.role = AccountRole.ATTENDANT;
        this.status = AccountStatus.UNVERIFIED;
        this.registrationTimestamp = Instant.now();
    }
}
