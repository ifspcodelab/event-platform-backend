package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.session.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    boolean existsByCpfAndIdNot(String cpf, UUID accountId);
    Optional<Account> findByEmail(String email);
    Optional<Account> findByCpf(String cpf);
    List<Account> findByNameStartingWithIgnoreCaseAndVerified(String name, boolean verified);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional<Account> findByIdWithPessimisticLock(UUID id);
}
