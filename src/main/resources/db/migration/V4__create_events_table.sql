CREATE TABLE events(
    id UUID NOT NULL,
    title VARCHAR NOT NULL UNIQUE,
    slug VARCHAR NOT NULL UNIQUE,
    summary VARCHAR NOT NULL,
    presentation TEXT NOT NULL,
    contact TEXT NOT NULL,
    registration_start_date DATE NOT NULL,
    registration_end_date DATE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    smaller_image VARCHAR,
    bigger_image VARCHAR,
    status VARCHAR NOT NULL,
    cancellation_message TEXT,
    CONSTRAINT events_pk PRIMARY KEY (id)
)