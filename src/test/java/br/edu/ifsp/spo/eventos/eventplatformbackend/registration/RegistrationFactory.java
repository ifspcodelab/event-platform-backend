package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionFactory;

public class RegistrationFactory {
    public static Registration sampleRegistrationWithConfirmedStatus() {
        return Registration.createWithConfirmedStatus(
                AccountFactory.sampleAccount(),
                SessionFactory.sampleSession()
        );
    }

    public static Registration sampleRegistrationWithConfirmedStatusInOtherSession() {
        return Registration.createWithConfirmedStatus(
                AccountFactory.sampleAccount(),
                SessionFactory.sampleSession2()
        );
    }

    public static Registration sampleRegistrationWithWaitingListStatus() {
        return Registration.createWithWaitingListdStatus(
                AccountFactory.sampleAccount(),
                SessionFactory.sampleSession()
        );
    }

    public static Registration sampleRegistrationWithCanceledByAdminStatus() {
        Registration registration = Registration.createWithWaitingListdStatus(
                AccountFactory.sampleAccount(),
                SessionFactory.sampleSession()
        );
        registration.setRegistrationStatus(RegistrationStatus.CANCELED_BY_ADMIN);
        return registration;
    }
}
