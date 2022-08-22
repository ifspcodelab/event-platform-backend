package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, UUID> {
    boolean existsByAccountAndEventId(Account account, UUID eventId);
    List<Organizer> findAllByEventId(UUID eventId);
    void deleteByAccount(Account account);
}
