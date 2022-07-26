CREATE TABLE accounts(
    id UUID NOT NULL,
    name VARCHAR NOT NULL,
    email VARCHAR NOT NULL UNIQUE,
    cpf VARCHAR NOT NULL UNIQUE,
    password CHAR(60) NOT NULL,
    agreed BOOLEAN NOT NULL,
    role VARCHAR NOT NULL,
    verified BOOLEAN NOT NULL,
    CONSTRAINT accounts_pk PRIMARY KEY (id)
)