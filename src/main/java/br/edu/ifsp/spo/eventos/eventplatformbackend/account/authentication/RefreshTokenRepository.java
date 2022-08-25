package br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    void deleteAllByAccountId(UUID accountId);
    RefreshToken findByAccountId(UUID accountId);
}
