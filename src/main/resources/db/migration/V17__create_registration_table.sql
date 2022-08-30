CREATE TABLE registrations(
    id UUID NOT NULL,
    date TIMESTAMP NOT NULL,
    account_id UUID NOT NULL,
    session_id UUID NOT NULL,
    event_id UUID NOT NULL,
    registration_status VARCHAR NOT NULL,
    time_email_was_sent TIMESTAMP,
    CONSTRAINT registration_pk PRIMARY KEY (id),
    CONSTRAINT accounts_fk FOREIGN KEY (account_id) REFERENCES accounts(id),
    CONSTRAINT sessions_fk FOREIGN KEY (session_id) REFERENCES sessions(id),
    CONSTRAINT events_fk FOREIGN KEY (event_id) REFERENCES events(id)
)