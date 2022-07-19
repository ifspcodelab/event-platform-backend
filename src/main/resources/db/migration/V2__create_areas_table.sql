CREATE TABLE areas(
    id UUID NOT NULL,
    name VARCHAR NOT NULL,
    reference VARCHAR,
    location_id UUID NOT NULL,
    CONSTRAINT areas_pk PRIMARY KEY (id),
    CONSTRAINT locations_fk FOREIGN KEY (location_id) REFERENCES locations(id)
)