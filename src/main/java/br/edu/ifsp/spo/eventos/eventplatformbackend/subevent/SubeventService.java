package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SubeventService {
    private final SubeventRepository subeventRepository;
    private final EventRepository eventRepository;

    public Subevent create(SubeventCreateDto dto, UUID eventId) {
        Event event = getEvent(eventId);

        if(subeventRepository.existsByTitleAndEvent(dto.getTitle(), event)) {
            throw new ResourceAlreadyExistsException(ResourceName.SUBEVENT.getName(),"title", dto.getTitle());
        }

        if(subeventRepository.existsBySlugAndEvent(dto.getSlug(), event)) {
            throw new ResourceAlreadyExistsException(ResourceName.SUBEVENT.getName(), "slug", dto.getSlug());
        }

        if(dto.getExecutionPeriod().getStartDate().isBefore(event.getExecutionPeriod().getStartDate())) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_BEFORE_EVENT);
        }

        if(dto.getExecutionPeriod().getEndDate().isAfter(event.getExecutionPeriod().getEndDate())) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_AFTER_EVENT);
        }

        Subevent subevent = new Subevent(
                dto.getTitle(),
                dto.getSlug(),
                dto.getSummary(),
                dto.getPresentation(),
                dto.getExecutionPeriod(),
                dto.getSmallerImage(),
                dto.getBiggerImage(),
                event
        );
        return subeventRepository.save(subevent);
    }

    public Subevent findById(UUID eventId, UUID subeventId) {
        // TODO: ERRO NA CHECAGEM DO RELACIONAMENTO ENTRE SUBEVENTO E EVENTO
        checksEventExists(eventId);
        checksIfSubeventIsAssociateToEvent(eventId);

        return getSubevent(subeventId);
    }

    public List<Subevent> findAll(UUID eventId) {
        checksEventExists(eventId);

        return subeventRepository.findAllByEventId(eventId);
    }

    private Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException(ResourceName.EVENT.getName(), eventId));
    }

    private Subevent getSubevent(UUID subeventId) {
        return subeventRepository.findById(subeventId).orElseThrow(() -> new ResourceNotFoundException(ResourceName.EVENT.getName(), subeventId));
    }

    private void checksEventExists(UUID eventId) {
        if(!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException(ResourceName.EVENT.getName(), eventId);
        }
    }

    private void checksIfSubeventIsAssociateToEvent(UUID eventId) {
        if (subeventRepository.existsByEventId(eventId)) {
            throw new BusinessRuleException(BusinessRuleType.SUBEVENT_IS_NOT_ASSOCIATED_EVENT);
        }
    }
}
