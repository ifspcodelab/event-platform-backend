package br.edu.ifsp.spo.eventos.eventplatformbackend.speaker;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.DiffResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class SpeakerService {
    private final SpeakerRepository speakerRepository;
    private final AccountRepository accountRepository;
    private final AuditService auditService;

    public Speaker create(SpeakerCreateDto dto) {
        if(speakerRepository.existsByCpf(dto.getCpf())) {
            throw new ResourceAlreadyExistsException(ResourceName.SPEAKER, "cpf", dto.getCpf());
        }

        if(speakerRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException(ResourceName.SPEAKER, "email", dto.getEmail());
        }

        Speaker speaker = dtoToSpeaker(dto);

        Optional<Account> optionalAccount = accountRepository.findByCpf(dto.getCpf());
        if(optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            speaker.setAccount(account);
            speakerRepository.save(speaker);

            log.info(
                "Speaker with name={}, email={} and accountId={} was created",
                speaker.getName(), speaker.getEmail(), account.getId()
            );

            return speaker;
        }

        speakerRepository.save(speaker);

        log.info("Speaker with name={} and email={} was created", speaker.getName(), speaker.getEmail());
        log.info("Speaker created without account association");

        return speaker;
    }

    public Speaker findById(UUID speakerId) {
        return getSpeaker(speakerId);
    }

    public Page<Speaker> findAll(Pageable pageable) {
        return speakerRepository.findAll(pageable);
    }

    public List<Speaker> findByName(String name) {
        return speakerRepository.findByNameStartingWithIgnoreCase(name);
    }

    public Speaker update(UUID speakerId, SpeakerCreateDto dto) {
        Speaker speaker = getSpeaker(speakerId);

        if(speakerRepository.existsByCpfAndIdNot(dto.getCpf(), speakerId)) {
            throw new ResourceAlreadyExistsException(ResourceName.SPEAKER, "cpf", dto.getCpf());
        }

        if(speakerRepository.existsByEmailAndIdNot(dto.getEmail(), speakerId)) {
            throw new ResourceAlreadyExistsException(ResourceName.SPEAKER, "email", dto.getEmail());
        }

        Speaker currentSpeaker = new Speaker();
        currentSpeaker.setName(speaker.getName());
        currentSpeaker.setEmail(speaker.getEmail());
        currentSpeaker.setCpf(speaker.getCpf());
        currentSpeaker.setCurriculum(speaker.getCurriculum());
        currentSpeaker.setLattes(speaker.getLattes());
        currentSpeaker.setLinkedin(speaker.getLinkedin());
        currentSpeaker.setPhoneNumber(speaker.getPhoneNumber());

        speaker.setName(dto.getName());
        speaker.setEmail(dto.getEmail());
        speaker.setCpf(dto.getCpf());
        speaker.setCurriculum(dto.getCurriculum());
        speaker.setLattes(dto.getLattes());
        speaker.setLinkedin(dto.getLinkedin());
        speaker.setPhoneNumber(dto.getPhoneNumber());

        DiffResult<?> diffResult = currentSpeaker.diff(speaker);

        Optional<Account> optionalAccount = accountRepository.findByCpf(dto.getCpf());
        if(optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            speaker.setAccount(account);
        }

        speaker = speakerRepository.save(speaker);
        log.info("Speaker with name={} and email={} was updated", speaker.getName(), speaker.getEmail());
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditService.logAdminUpdate(jwtUserDetails.getId(), ResourceName.SPEAKER, diffResult.getDiffs().toString(), speakerId);

        return speaker;
    }

    public void delete(UUID speakerId) {
        Speaker speaker = getSpeaker(speakerId);
        speakerRepository.deleteById(speakerId);
        log.info("Delete speaker id={}, name={}, email={}", speaker.getId(), speaker.getName(), speaker.getEmail());
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditService.logAdminDelete(jwtUserDetails.getId(), ResourceName.SPEAKER, speakerId);
    }

    private Speaker getSpeaker(UUID speakerId) {
        return speakerRepository.findById(speakerId)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SPEAKER, speakerId));
    }

    private Speaker dtoToSpeaker(SpeakerCreateDto dto) {
        return new Speaker(
            dto.getName(),
            dto.getEmail(),
            dto.getCpf(),
            dto.getCurriculum(),
            dto.getLattes(),
            dto.getLinkedin(),
            dto.getPhoneNumber()
        );
    }
}
