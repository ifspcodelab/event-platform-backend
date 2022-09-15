package br.edu.ifsp.spo.eventos.eventplatformbackend.account.invalidemail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InvalidEmailRepository extends JpaRepository<InvalidEmail, UUID> {
    boolean existsByEmail(String email);
}
