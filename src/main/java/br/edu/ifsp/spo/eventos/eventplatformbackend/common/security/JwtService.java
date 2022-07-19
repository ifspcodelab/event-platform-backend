package br.edu.ifsp.spo.eventos.eventplatformbackend.common.security;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@AllArgsConstructor
public class JwtService {
    private JwtConfig jwtConfig;

    public String generateJwt(Account account){
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        Date now = new Date();

        JWTCreator.Builder builder = JWT.create();
        builder.withSubject(account.getId().toString());
        builder.withIssuer(jwtConfig.getIssuer());
        builder.withIssuedAt(now);
        builder.withExpiresAt(new Date(now.getTime() + jwtConfig.getExpiresIn() * 1_000L));
        builder.withClaim("email", account.getEmail());
        builder.withClaim("role", account.getRole());

        return builder.sign(algorithm);
    }
}
