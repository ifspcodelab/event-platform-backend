CREATE TABLE sessions_schedules(
    id UUID NOT NULL,
    execution_start TIMESTAMP NOT NULL,
    execution_end TIMESTAMP NOT NULL,
    url VARCHAR,
    location_id UUID,
    area_id UUID,
    space_id UUID,
    session_id UUID NOT NULL,
    CONSTRAINT sessions_schedules_pk PRIMARY KEY (id),
    CONSTRAINT locations_fk FOREIGN KEY (location_id) REFERENCES locations(id),
    CONSTRAINT areas_fk FOREIGN KEY (area_id) REFERENCES areas(id),
    CONSTRAINT spaces_fk FOREIGN KEY (space_id) REFERENCES spaces(id),
    CONSTRAINT sessions_fk FOREIGN KEY (session_id) REFERENCES sessions(id)
)