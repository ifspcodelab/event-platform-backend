CREATE TABLE verification_tokens(
    id UUID NOT NULL,
    token UUID NOT NULL,
    expires_in TIMESTAMP NOT NULL,
    account_id UUID NOT NULL,
    CONSTRAINT verification_tokens_pk PRIMARY KEY (id),
    CONSTRAINT accounts_fk FOREIGN KEY (account_id) REFERENCES accounts(id)
)