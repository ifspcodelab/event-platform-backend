package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

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
@Table(name = "verification_tokens")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VerificationToken {
    @Id
    private UUID id;
    private UUID token;
    private Instant expiresIn;
    @ManyToOne
    private Account account;

    public VerificationToken(Account account, Integer durationInSeconds) {
        this.id = UUID.randomUUID();
        this.token = UUID.randomUUID();
        this.expiresIn = Instant.now().plusSeconds(durationInSeconds);
        this.account = account;
    }
}
