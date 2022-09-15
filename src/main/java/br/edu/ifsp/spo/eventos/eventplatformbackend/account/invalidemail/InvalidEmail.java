package br.edu.ifsp.spo.eventos.eventplatformbackend.account.invalidemail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "invalid_emails")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InvalidEmail {
    @Id
    private UUID id;
    private String email;
}
