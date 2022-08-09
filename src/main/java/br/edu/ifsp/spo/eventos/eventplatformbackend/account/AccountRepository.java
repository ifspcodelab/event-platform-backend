package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);


    Optional<Account> findByEmail(String email);
    Optional<Account> findByCpf(String cpf);

    Optional<Account> findByName(String userName);

    Page<Account> findAllByName(Pageable pageable, String name);

    Page<Account> findAllByEmail(Pageable pageable, String email);

    Page<Account> findAllByCpf(Pageable pageable, String cpf);
}
