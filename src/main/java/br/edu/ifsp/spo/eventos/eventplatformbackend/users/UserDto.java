package br.edu.ifsp.spo.eventos.eventplatformbackend.users;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;


import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private Boolean agreed;
    private String role;
    private Boolean verified;
    private Instant registrationTimestamp;

}
