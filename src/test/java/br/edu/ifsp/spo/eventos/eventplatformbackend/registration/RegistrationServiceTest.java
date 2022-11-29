package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.attendance.AttendanceRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.email.EmailService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private RegistrationRepository registrationRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private EmailService emailService;
    @Value("${registration.email-confirmation-time}")
    private String emailConfirmationTime;
    @InjectMocks
    private RegistrationService registrationService;
    private Registration registrationConfirmedStatus;
    private Registration registrationWaitingListStatus;


    @BeforeEach
    public void setUp() {
        registrationConfirmedStatus = RegistrationFactory.sampleRegistrationWithConfirmedStatus();
        registrationWaitingListStatus = RegistrationFactory.sampleRegistrationWithWaitingListStatus();
    }

//    @Test
//    public void create_ThrowsException_WhenThereIsNoAreaPersisted() {
//        UUID locationId = location.getId();
//        UUID areaId = area.getId();
//
//        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
//
//        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
//                () -> spaceService.create(locationId, areaId, spaceCreateDto)
//        );
//        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
//        assertThat(exception.getResourceId()).isEqualTo(areaId.toString());
//        assertThat(exception.getResourceName()).isEqualTo(ResourceName.AREA);
//    }
}
