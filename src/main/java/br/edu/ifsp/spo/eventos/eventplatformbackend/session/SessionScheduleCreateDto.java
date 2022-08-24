package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;
import lombok.Value;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class SessionScheduleCreateDto {
    @NotNull
    LocalDateTime executionStart;
    @NotNull
    LocalDateTime executionEnd;
    @URL
    String url;
    UUID locationId;
    UUID areaId;
    UUID spaceId;

    // SS                              s-----e
    // PI           s---------------------e
    // PE                     s-------------------e



//    if(activity.isNeedRegistration() && createDto.getExecutionStart().toLocalDate().isBefore(event.getExecutionPeriod().getStartDate())) {
//        throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_BEFORE_EVENT_EXECUTION);
//    }
}
