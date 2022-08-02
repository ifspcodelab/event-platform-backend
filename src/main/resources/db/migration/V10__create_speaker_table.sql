CREATE TABLE speakers(
    id UUID NOT NULL,
    name VARCHAR NOT NULL,
    email VARCHAR NOT NULL UNIQUE,
    cpf VARCHAR NOT NULL UNIQUE,
    curriculum VARCHAR NOT NULL,
    lattes VARCHAR,
    linkedin VARCHAR,
    phone_number VARCHAR,
    account_id UUID NOT NULL,
    CONSTRAINT speaker_pk PRIMARY KEY (id),
    CONSTRAINT accounts_fk FOREIGN KEY (account_id) REFERENCES accounts(id)
)