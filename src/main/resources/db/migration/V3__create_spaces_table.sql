CREATE TABLE spaces(
    id UUID NOT NULL,
    name VARCHAR NOT NULL UNIQUE,
    capacity SMALLINT NOT NULL,
    space_type VARCHAR NOT NULL,
    area_id UUID NOT NULL,
    CONSTRAINT spaces_pk PRIMARY KEY (id),
    CONSTRAINT areas_fk FOREIGN KEY (area_id) REFERENCES areas(id)
)