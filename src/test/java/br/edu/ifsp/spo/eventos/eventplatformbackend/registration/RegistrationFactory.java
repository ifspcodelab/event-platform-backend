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

    public static Registration sampleRegistrationWithWaitingListStatus() {
        return Registration.createWithWaitingListdStatus(
                AccountFactory.sampleAccount(),
                SessionFactory.sampleSession()
        );
    }
}
