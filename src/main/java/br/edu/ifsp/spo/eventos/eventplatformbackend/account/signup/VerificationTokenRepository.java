package br.edu.ifsp.spo.eventos.eventplatformbackend.account.signup;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findByToken(UUID token);
    List<VerificationToken> findAllByExpiresInBefore(Instant instant);
    VerificationToken findByAccount(Account account);
    Boolean existsByAccount(Account account);
    Boolean existsByExpiresInAfter(Instant instant);
}
