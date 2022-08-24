package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Account implements Diffable<Account> {
    @Id
    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private String password;
    private Boolean agreed;
    @Enumerated(EnumType.STRING)
    private AccountRole role;
    private Boolean verified;
    private Instant registrationTimestamp;

    public Account(String name, String email, String cpf, String password, Boolean agreed) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.password = password;
        this.agreed = agreed;
        this.role = AccountRole.ATTENDANT;
        this.verified = false;
        this.registrationTimestamp = Instant.now();
    }

    @Override
    public DiffResult<Account> diff(Account updatedAccount) {
        return new DiffBuilder<>(this, updatedAccount, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Nome", this.name, updatedAccount.name)
                .append("email", this.email, updatedAccount.email)
                .append("CPF", this.cpf, updatedAccount.cpf)
                .build();
    }
}
