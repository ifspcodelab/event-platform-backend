package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, UUID> {
    Integer countRegistrationsBySessionIdAndRegistrationStatus(UUID sessionId, RegistrationStatus registrationStatus);
    boolean existsBySessionIdAndAccountIdAndRegistrationStatus(UUID sessionId, UUID accountId, RegistrationStatus registrationStatus);
    List<Registration> findAllByAccountIdAndRegistrationStatus(UUID accountId, RegistrationStatus registrationStatus);
    List<Registration> findAllBySessionId(UUID sessionId);
    Optional<Registration> getFirstByRegistrationStatus(RegistrationStatus registrationStatus);
}
