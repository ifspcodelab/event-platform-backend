package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
    List<Account> findAllByVerified(Boolean verified);
}
