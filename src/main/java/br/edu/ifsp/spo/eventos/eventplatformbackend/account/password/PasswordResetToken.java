package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;


import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PasswordResetToken {

    @Id
    private UUID id;
    private Instant expiresIn;
    private UUID token;
    @ManyToOne
    private Account account;

    public PasswordResetToken(Account account, Integer durationInSeconds) {
        this.id = UUID.randomUUID();
        this.token = UUID.randomUUID();
        this.expiresIn = Instant.now().plusSeconds(durationInSeconds);
        this.account = account;
    }

    public boolean isExpired(){
        return this.expiresIn.isBefore(Instant.now());
    }

}
