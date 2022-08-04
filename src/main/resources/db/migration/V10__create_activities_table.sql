CREATE TABLE activities(
   id UUID NOT NULL,
   title VARCHAR NOT NULL,
   slug VARCHAR NOT NULL,
   description VARCHAR NOT NULL,
   type VARCHAR NOT NULL,
   status VARCHAR NOT NULL,
   online BOOLEAN NOT NULL,
   need_registration BOOLEAN NOT NULL,
   event_id UUID NOT NULL,
   subevent_id UUID,
   CONSTRAINT activities_pk PRIMARY KEY (id),
   CONSTRAINT events_fk FOREIGN KEY (event_id) REFERENCES events(id),
   CONSTRAINT subevents_fk FOREIGN KEY (subevent_id) REFERENCES subevents(id)
)