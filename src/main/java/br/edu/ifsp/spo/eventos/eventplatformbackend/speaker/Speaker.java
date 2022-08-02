package br.edu.ifsp.spo.eventos.eventplatformbackend.speaker;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "speakers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Speaker {
    @Id
    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private String curriculum;
    private String lattes;
    private String linkedin;
    private String phoneNumber;
    @OneToOne
    private Account account;

    public Speaker(String name, String email, String cpf, String curriculum, String lattes, String linkedin, String phoneNumber) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.curriculum = curriculum;
        this.lattes = lattes;
        this.linkedin = linkedin;
        this.phoneNumber = phoneNumber;
    }

    public Speaker(String name, String email, String cpf, String curriculum, String lattes, String linkedin, String phoneNumber, Account account) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.curriculum = curriculum;
        this.lattes = lattes;
        this.linkedin = linkedin;
        this.phoneNumber = phoneNumber;
        this.account = account;
    }
}
