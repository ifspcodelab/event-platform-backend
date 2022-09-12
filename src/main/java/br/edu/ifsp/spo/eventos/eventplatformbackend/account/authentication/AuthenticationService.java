package br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.RecaptchaException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.RecaptchaExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.RecaptchaService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer.OrganizerRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent.OrganizerSubeventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent.OrganizerSubeventService;
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
    private final OrganizerRepository organizerRepository;
    private final OrganizerSubeventRepository organizerSubeventRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RecaptchaService recaptchaService;
    private final AuditService auditService;

    @Transactional
    public TokensDto login(LoginCreateDto loginCreateDto){
        if (!recaptchaService.isValid(loginCreateDto.getRecaptcha())) {
            throw new RecaptchaException(RecaptchaExceptionType.INVALID_RECAPTCHA, loginCreateDto.getEmail());
        }

        Account account = getAccount(loginCreateDto.getEmail());

        if (!account.getVerified()) {
            throw new AuthenticationException(AuthenticationExceptionType.UNVERIFIED_ACCOUNT, account.getEmail());
        }

        if (!passwordEncoder.matches(loginCreateDto.getPassword(), account.getPassword())) {
            throw new AuthenticationException(AuthenticationExceptionType.INCORRECT_PASSWORD, loginCreateDto.getEmail());
        }

        refreshTokenRepository.deleteAllByAccountId(account.getId());

        String accessTokenString;

        boolean isEventOrganizer = organizerRepository.existsByAccountId(account.getId());
        boolean isSubeventOrganizer = organizerSubeventRepository.existsByAccountId(account.getId());
        if (isEventOrganizer && isSubeventOrganizer) {
            var organizerEventIds = organizerRepository.findAllEventIdByAccountId(account.getId());
            var organizerSubeventIds = organizerSubeventRepository.findAllSubeventIdByAccountId(account.getId());
            accessTokenString = jwtService.generateOrganizerEventSubeventAccessToken(account, organizerEventIds, organizerSubeventIds);
        } else if (isEventOrganizer) {
            var organizerEventIds = organizerRepository.findAllEventIdByAccountId(account.getId());
            accessTokenString = jwtService.generateOrganizerAccessToken(account, organizerEventIds);
        } else if (isSubeventOrganizer) {
            var organizerSubeventIds = organizerSubeventRepository.findAllSubeventIdByAccountId(account.getId());
            accessTokenString = jwtService.generateOrganizerSubeventAccessToken(account, organizerSubeventIds);
        } else {
            accessTokenString = jwtService.generateAccessToken(account);
        }

        UUID refreshTokenId = UUID.randomUUID();
        String refreshTokenString = jwtService.generateRefreshToken(account, refreshTokenId);
        RefreshToken refreshToken = new RefreshToken(refreshTokenId, refreshTokenString, account);
        refreshTokenRepository.save(refreshToken);

        TokensDto tokensDto = new TokensDto(accessTokenString, refreshTokenString);

        log.info("Successful login for the email {}", account.getEmail());
        auditService.logCreate(account, ResourceName.REFRESH_TOKEN, "Login", refreshToken.getId());

        return tokensDto;
    }

    @Transactional
    public void logout(JwtUserDetails jwtUserDetails) {
        UUID accountId = jwtUserDetails.getId();
        String accountEmail = jwtUserDetails.getUsername();

        UUID refreshTokenId = refreshTokenRepository.findByAccountId(accountId).getId();
        refreshTokenRepository.deleteAllByAccountId(accountId);

        log.info("Successful logout for the email {}", accountEmail);

        auditService.logDelete(getAccount(accountId), ResourceName.REFRESH_TOKEN, "Desconectou da aplicação", refreshTokenId);
    }

    @Transactional
    public TokensDto rotateRefreshToken(RefreshTokenRotateDto refreshTokenRotateDto) {
        String refreshTokenString = refreshTokenRotateDto.getRefreshToken();

        DecodedJWT decodedToken = jwtService.decodeToken(refreshTokenString);

        UUID tokenId = UUID.fromString(decodedToken.getId());
        UUID accountId = UUID.fromString(decodedToken.getSubject());

        Account account = getAccount(accountId);

        if(!refreshTokenRepository.existsById(tokenId)) {
            throw new AuthenticationException(AuthenticationExceptionType.NONEXISTENT_TOKEN, account.getEmail());
        }

        UUID refreshTokenId = UUID.randomUUID();
        String newAccessTokenString = jwtService.generateAccessToken(account);
        String newRefreshTokenString = jwtService.generateRefreshToken(account, refreshTokenId);

        refreshTokenRepository.updateTokenByAccountId(refreshTokenId, newRefreshTokenString, account);

        TokensDto tokensDto = new TokensDto(newAccessTokenString, newRefreshTokenString);

        log.info("Successful token rotation for the email {}", account.getEmail());

        return tokensDto;
    }

    private Account getAccount(String email) {
        return accountRepository.findByEmail(email).orElseThrow(
                () -> new AuthenticationException(AuthenticationExceptionType.NONEXISTENT_ACCOUNT_BY_EMAIL, email)
        );
    }

    private Account getAccount(UUID id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new AuthenticationException(AuthenticationExceptionType.NONEXISTENT_ACCOUNT_BY_ID, id.toString())
        );
    }
}
