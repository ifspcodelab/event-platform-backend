package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);

    Optional<Account> findByEmail(String email);
    Optional<Account> findByCpf(String cpf);

    @Query("SELECT a FROM Account a WHERE UPPER(a.name) LIKE CONCAT('%',UPPER(:name),'%')")
    Page<Account> findUsersWithPartOfName(Pageable pageable, String name);

    @Query("SELECT a FROM Account a WHERE UPPER(a.email) LIKE CONCAT('%',UPPER(:email),'%')")
    Page<Account> findUsersWithPartOfEmail(Pageable pageable, String email);

    @Query("SELECT a FROM Account a WHERE a.cpf LIKE CONCAT('%',:cpf,'%')")
    Page<Account> findUsersWithPartOfCpf(Pageable pageable, String cpf);
}
