package br.edu.ifsp.spo.eventos.eventplatformbackend.account.deletion;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.password.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AccountDeletionTokenRepository extends JpaRepository<AccountDeletionToken, UUID> {

    boolean existsByAccountAndExpiresInAfter(Account account, Instant now);

    Optional<AccountDeletionToken> findByToken(UUID token);

    boolean existsByTokenAndExpiresInBefore(UUID token, Instant now);

    boolean existsByToken(UUID token);

}
