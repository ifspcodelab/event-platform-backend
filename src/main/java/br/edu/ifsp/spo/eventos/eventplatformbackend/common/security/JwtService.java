package br.edu.ifsp.spo.eventos.eventplatformbackend.common.security;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class JwtService {
    private JwtConfig jwtConfig;

    public String generateAccessToken(
        Account account,
        List<String> collaboratorEventIds,
        List<String> collaboratorSubEventIds,
        List<String> coordinatorEventIds,
        List<String> ccoordinatorSubEventIds
    ) {
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        Instant now = Instant.now();

        JWTCreator.Builder builder = JWT.create();
        builder.withIssuer(jwtConfig.getIssuer());
        builder.withSubject(account.getId().toString());
        builder.withIssuedAt(now);
        builder.withExpiresAt(now.plusSeconds(jwtConfig.getAccessTokenExpiresIn()));
        builder.withClaim("email", account.getEmail());
        builder.withClaim("role", account.getRole().name());
        builder.withClaim("collaboratorEvent", collaboratorEventIds);
        builder.withClaim("collaboratorSubevent", collaboratorSubEventIds);
        builder.withClaim("coordinatorEvent", coordinatorEventIds);
        builder.withClaim("coordinatorSubevent", ccoordinatorSubEventIds);

        return builder.sign(algorithm);
    }

    public String generateRefreshToken(Account account, UUID jwtId) {
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        Instant now = Instant.now();

        JWTCreator.Builder builder = JWT.create();
        builder.withIssuer(jwtConfig.getIssuer());
        builder.withSubject(account.getId().toString());
        builder.withIssuedAt(now);
        builder.withExpiresAt(now.plusSeconds(jwtConfig.getRefreshTokenExpiresIn()));
        builder.withJWTId(jwtId.toString());

        return builder.sign(algorithm);
    }

    public DecodedJWT decodeToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(jwtConfig.getIssuer()).build();

        DecodedJWT decodedJWT = verifier.verify(token);

        return decodedJWT;
    }
}
