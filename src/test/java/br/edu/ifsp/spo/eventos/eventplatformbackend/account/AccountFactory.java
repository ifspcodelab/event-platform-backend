package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import java.time.Instant;

public class AccountFactory {
    public static Account sampleAccount() {
        return new Account(
                "Marcelo Silva",
                "marcelo01@email.com",
                "66709094030",
                "$2a$10$V/3zrFYbQhhJI1A3s1ve9OOHi62D.WqBAjgOSg5rOafrB50hvo30S",
                true
        );
    }
}
