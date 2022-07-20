package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    boolean existsByAccountAndExpiresInAfter(Account account, Instant now);

    Optional<PasswordResetToken> findByToken(UUID token);

    boolean existsByTokenAndExpiresInBefore(UUID token, Instant now);

    boolean existsByToken(UUID token);

}
