package br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LogRepository extends JpaRepository<Log, UUID> {
    List<Log> findAllByAccountIdAndResourceNameInOrderByCreatedAtDesc(UUID id, List<ResourceName> resourceNames);
    void deleteAllByAccount(Account account);
}
