CREATE TABLE accounts(
    id UUID NOT NULL,
    name VARCHAR NOT NULL,
    email VARCHAR NOT NULL UNIQUE,
    cpf VARCHAR NOT NULL UNIQUE,
    password VARCHAR NOT NULL,
    agreed BOOLEAN NOT NULL,
    role VARCHAR NOT NULL,
    CONSTRAINT accounts_pk PRIMARY KEY (id)
)