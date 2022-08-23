package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizerSubeventRepository extends JpaRepository<OrganizerSubevent, UUID> {
    List<OrganizerSubevent> findAllBySubeventId(UUID subeventId);
    boolean existsByAccountAndSubeventId(Account account, UUID subeventId);
}
