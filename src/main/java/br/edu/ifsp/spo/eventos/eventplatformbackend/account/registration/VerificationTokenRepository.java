package br.edu.ifsp.spo.eventos.eventplatformbackend.account.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findByToken(UUID token);
    void deleteAllByExpiresInBefore(Instant instant);
}
