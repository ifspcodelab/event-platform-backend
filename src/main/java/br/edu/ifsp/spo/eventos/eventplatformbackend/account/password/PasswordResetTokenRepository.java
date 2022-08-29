package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    boolean existsByAccountAndExpiresInAfter(Account account, Instant now);
    Optional<PasswordResetToken> findByToken(UUID token);
    Iterable<PasswordResetToken> findAllByExpiresInBefore(Instant now);
}
