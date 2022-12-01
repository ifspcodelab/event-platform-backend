package br.edu.ifsp.spo.eventos.eventplatformbackend.common.validators;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

public class JwtUserDetailsFactory {
    public static JwtUserDetails sampleJwtUserDetailsThatIsNotOrganizer() {
        return new JwtUserDetails(
                UUID.randomUUID(),
                "username",
                List.of(
                        new SimpleGrantedAuthority("ROLE_ATTENDANT")
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of()
                );
    }

    public static JwtUserDetails sampleJwtUserDetailsThatIsOrganizer(UUID eventId) {
        return new JwtUserDetails(
                UUID.randomUUID(),
                "username",
                List.of(
                        new SimpleGrantedAuthority("ROLE_ATTENDANT")
                ),
                List.of(eventId.toString()),
                List.of(),
                List.of(),
                List.of()
        );
    }
}
