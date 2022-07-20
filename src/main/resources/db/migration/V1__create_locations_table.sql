CREATE TABLE locations(
    id UUID NOT NULL,
    name VARCHAR NOT NULL UNIQUE,
    address VARCHAR NOT NULL,
    CONSTRAINT locations_pk PRIMARY KEY (id)
)