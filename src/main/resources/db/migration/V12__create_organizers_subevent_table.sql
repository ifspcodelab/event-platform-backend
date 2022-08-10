CREATE TABLE organizers_subevent(
    id UUID NOT NULL,
    type VARCHAR NOT NULL,
    account_id UUID NOT NULL,
    event_id UUID NOT NULL,
    subevent_id UUID NOT NULL,
    CONSTRAINT organizers_subevent_pk PRIMARY KEY (id),
    CONSTRAINT accounts_fk FOREIGN KEY (account_id) REFERENCES accounts(id),
    CONSTRAINT events_fk FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT subevents_fk FOREIGN KEY (subevent_id) REFERENCES subevents(id)
)