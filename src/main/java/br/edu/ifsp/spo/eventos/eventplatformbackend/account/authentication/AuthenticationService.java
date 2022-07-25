package br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtService;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TokensDto login(LoginCreateDto loginCreateDto){
        Account account = getAccount(loginCreateDto.getEmail());

        if (!account.getVerified()) {
            throw new LoginException(LoginExceptionType.UNVERIFIED_ACCOUNT, account.getEmail());
        }

        if (!passwordEncoder.matches(loginCreateDto.getPassword(), account.getPassword())) {
            throw new LoginException(LoginExceptionType.INCORRECT_PASSWORD, loginCreateDto.getEmail());
        }

        refreshTokenRepository.deleteAllByAccountId(account.getId());

        UUID refreshTokenId = UUID.randomUUID();
        String accessTokenString = jwtService.generateAccessToken(account);
        String refreshTokenString = jwtService.generateRefreshToken(account, refreshTokenId);

        RefreshToken refreshToken = new RefreshToken(refreshTokenId, refreshTokenString, account);

        refreshTokenRepository.save(refreshToken);

        TokensDto tokensDto = new TokensDto(accessTokenString, refreshTokenString);

        log.info("Successful login for the email {}", account.getEmail());

        return tokensDto;
    }

    private Account getAccount(String email) {
        return accountRepository.findByEmail(email).orElseThrow(
                () -> new LoginException(LoginExceptionType.NONEXISTENT_ACCOUNT, email)
        );
    }

    @Transactional
    public void logout(String accessToken) {
        DecodedJWT decodedToken = jwtService.decodeToken(accessToken);
        UUID accountId = UUID.fromString(decodedToken.getSubject());
        String accountEmail = decodedToken.getClaim("email").asString();

        refreshTokenRepository.deleteAllByAccountId(accountId);

        log.info("Successful logout for the email {}", accountEmail);
    }
}
