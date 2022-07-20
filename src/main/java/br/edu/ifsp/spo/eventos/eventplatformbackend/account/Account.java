package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
    @Size(max = 350)
    private String email;
    private String cpf;
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$", flags = Pattern.Flag.UNICODE_CASE, message = "A senha deve ter uma maiúscula, uma minúscula, um número e um caractere especial")
    private String password;
    @AssertTrue(message = "Os termos devem estar aceitos")
    private Boolean agreed;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private Boolean verified;

    public Account(String name, String email, String cpf, String password, Boolean agreed) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.password = password;
        this.agreed = agreed;
        this.role = UserRole.ATTENDANT;
        this.verified = false;
    }
}
