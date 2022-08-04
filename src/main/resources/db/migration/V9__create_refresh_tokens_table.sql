CREATE TABLE refresh_tokens(
    id UUID NOT NULL,
    token VARCHAR NOT NULL,
    account_id UUID NOT NULL,
    CONSTRAINT refresh_tokens_pk PRIMARY KEY (id),
    CONSTRAINT accounts_fk FOREIGN KEY (account_id) REFERENCES accounts(id)
)