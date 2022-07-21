package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.LoginException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.RefreshToken;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final AccountConfig accountConfig;
    private final VerificationTokenRepository verificationTokenRepository;

    @Transactional
    public Account create(AccountCreateDto dto) {
        if(accountRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException("account", "e-mail", dto.getEmail());
        }
        if(accountRepository.existsByCpf(dto.getCpf())) {
            throw new ResourceAlreadyExistsException("account", "cpf", dto.getCpf());
        }

        Account account = new Account(
                dto.getName(),
                dto.getEmail(),
                dto.getCpf(),
                dto.getPassword(),
                dto.getAgreed()
        );

        account.setVerified(true);

        account = accountRepository.save(account);

        VerificationToken verificationToken = new VerificationToken(account,accountConfig.getVerificationTokenExpiresIn());

        verificationTokenRepository.save(verificationToken);

        return account;
    }

    public TokensDto login(LoginCreateDto loginCreateDto){
        Account account = getAccount(loginCreateDto.getEmail());
        isVerified(account);
        comparesPassword(account, loginCreateDto.getPassword());

        UUID refreshTokenId = UUID.randomUUID();
        String accessTokenString = jwtService.generateAccessToken(account);
        String refreshTokenString = jwtService.generateRefreshToken(account, refreshTokenId);

        RefreshToken refreshToken = new RefreshToken(refreshTokenId, refreshTokenString, account);

        refreshTokenRepository.save(refreshToken);

        TokensDto tokensDto = new TokensDto(accessTokenString, refreshTokenString);

        return tokensDto;
    }

    private Account getAccount(String email){
        return accountRepository.findByEmail(email).orElseThrow(
                () -> new LoginException(String.format("Login Exception email %s not found", email))
        );
    }

    private void comparesPassword(Account account, String password)
    {
        boolean passwordComparison = account.getPassword().equals(password);
        if (!passwordComparison)
        {
            throw new LoginException("Login Exception the entered password is incorrect");
        }
    }

    private void isVerified(Account account)
    {
        if (!account.getVerified())
        {
            throw new LoginException(String.format("Login Exception the account for the email %s is not yet verified", account.getEmail()));
        }
    }
}
