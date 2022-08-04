CREATE TABLE subevents(
    id UUID NOT NULL,
    title VARCHAR NOT NULL,
    slug VARCHAR NOT NULL,
    summary VARCHAR NOT NULL,
    presentation TEXT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    smaller_image VARCHAR,
    bigger_image VARCHAR,
    status VARCHAR NOT NULL,
    event_id UUID NOT NULL,
    cancellation_message TEXT,
    CONSTRAINT subevents_pk PRIMARY KEY (id),
    CONSTRAINT events_fk FOREIGN KEY (event_id) REFERENCES events(id)
)