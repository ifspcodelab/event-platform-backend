package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

public class JwtUserDetailsFactory {

    public static JwtUserDetails sampleJwtUserDetailsNonAdmin() {
        return new JwtUserDetails(
                UUID.randomUUID(),
                "username",
                List.of(
                        new SimpleGrantedAuthority("ROLE_ATTENDANT")
                ),
                null,
                null,
                null,
                null
        );
    }

    public static JwtUserDetails sampleJwtUserDetailsAdmin() {
        return new JwtUserDetails(
                UUID.randomUUID(),
                "username",
                List.of(
                        new SimpleGrantedAuthority("ROLE_ATTENDANT"),
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                ),
                null,
                null,
                null,
                null
        );
    }
}
