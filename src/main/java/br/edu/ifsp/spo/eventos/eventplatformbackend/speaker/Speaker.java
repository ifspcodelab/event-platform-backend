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
    private String phone_number;
    @OneToOne
    private Account account;
}
