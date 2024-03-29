package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import javax.persistence.LockModeType;
import javax.persistence.NamedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    boolean existsByCpfAndIdNot(String cpf, UUID accountId);
    boolean existsByEmailAndIdNot(String email, UUID accountId);
    Optional<Account> findByEmail(String email);
    Optional<Account> findByCpf(String cpf);
    List<Account> findByNameStartingWithIgnoreCaseAndStatus(String name, AccountStatus status);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional<Account> findByIdWithPessimisticLock(UUID id);
    @Query("SELECT a FROM Account a WHERE UPPER(a.name) LIKE CONCAT('%',UPPER(:name),'%')")
    Page<Account> findUsersWithPartOfName(Pageable pageable, String name);
    @Query("SELECT a FROM Account a WHERE UPPER(a.email) LIKE CONCAT('%',UPPER(:email),'%')")
    Page<Account> findUsersWithPartOfEmail(Pageable pageable, String email);
    @Query("SELECT a FROM Account a WHERE a.cpf LIKE CONCAT('%',:cpf,'%')")
    Page<Account> findUsersWithPartOfCpf(Pageable pageable, String cpf);
    @Query("SELECT a FROM Account a WHERE a.cpf = :cpf AND a.status = br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountStatus.UNVERIFIED")
    Optional<Account> findByCpfAndStatusUnverified(String cpf);
    @Query("SELECT a FROM Account a WHERE a.email = :email AND a.status = br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountStatus.UNVERIFIED")
    Optional<Account> findByEmailAndStatusUnverified(String email);
}
