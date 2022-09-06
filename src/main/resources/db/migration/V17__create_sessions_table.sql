CREATE TABLE sessions(
       id UUID NOT NULL,
       title VARCHAR NOT NULL,
       seats SMALLINT NOT NULL,
       cancellation_message TEXT,
       canceled BOOLEAN NOT NULL,
       activity_id UUID NOT NULL,
       CONSTRAINT sessions_pk PRIMARY KEY (id),
       CONSTRAINT activities_fk FOREIGN KEY (activity_id) REFERENCES activities(id)
)