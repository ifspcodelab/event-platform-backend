CREATE TABLE activities(
   id UUID NOT NULL,
   title VARCHAR NOT NULL,
   slug VARCHAR NOT NULL,
   description TEXT NOT NULL,
   type VARCHAR NOT NULL,
   status VARCHAR NOT NULL,
   modality VARCHAR NOT NULL,
   need_registration BOOLEAN NOT NULL,
   duration INT NOT NULL,
   setup_time INT NOT NULL,
   cancellation_message TEXT,
   event_id UUID NOT NULL,
   subevent_id UUID,
   CONSTRAINT activities_pk PRIMARY KEY (id),
   CONSTRAINT events_fk FOREIGN KEY (event_id) REFERENCES events(id),
   CONSTRAINT subevents_fk FOREIGN KEY (subevent_id) REFERENCES subevents(id)
)