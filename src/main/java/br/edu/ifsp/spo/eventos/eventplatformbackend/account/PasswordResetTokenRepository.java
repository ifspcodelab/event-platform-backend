package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    boolean existsByAccountAndExpiresInAfter(Account account, Instant now);
}
