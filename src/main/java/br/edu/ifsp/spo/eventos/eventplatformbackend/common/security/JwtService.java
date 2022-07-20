package br.edu.ifsp.spo.eventos.eventplatformbackend.common.security;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@AllArgsConstructor
public class JwtService {
    private JwtConfig jwtConfig;

    public String generateAccessToken(Account account){
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        Instant now = Instant.now();

        JWTCreator.Builder builder = JWT.create();
        builder.withSubject(account.getId().toString());
        builder.withIssuer(jwtConfig.getIssuer());
        builder.withIssuedAt(now);
        builder.withExpiresAt(now.plusSeconds(jwtConfig.getAccessTokenExpiresIn()));
        builder.withClaim("email", account.getEmail());
        builder.withClaim("role", account.getRole());

        return builder.sign(algorithm);
    }

    public String generateRefreshToken(Account account, UUID jwtId){
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        Instant now = Instant.now();

        JWTCreator.Builder builder = JWT.create();
        builder.withSubject(account.getId().toString());
        builder.withIssuer(jwtConfig.getIssuer());
        builder.withIssuedAt(now);
        builder.withExpiresAt(now.plusSeconds(jwtConfig.getRefreshTokenExpiresIn()));
        builder.withJWTId(jwtId.toString());

        return builder.sign(algorithm);
    }
}
