package br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "logs")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Log {
    @Id
    private UUID id;
    private Instant createdAt;
    @ManyToOne
    private Account account;
    @Enumerated(EnumType.STRING)
    private Action action;
    @Enumerated(EnumType.STRING)
    private ResourceName resourceName;
    private String resourceData;

    public Log(Account account, Action action, ResourceName resourceName, String resourceData) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.account = account;
        this.action = action;
        this.resourceName = resourceName;
        this.resourceData = resourceData;
    }
}
