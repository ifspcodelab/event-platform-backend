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
    private Registration registration;

//    @BeforeEach
//    public void setUp() {
//        registration = RegistrationFactory.sampleRegistration();
//    }
}
