package br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    void deleteAllByAccountId(UUID accountId);
    @Modifying
    @Query("update RefreshToken t set t.id = :id, t.token = :token where t.account = :account")
    void updateTokenByAccountId(UUID id, String token, Account account);
}
