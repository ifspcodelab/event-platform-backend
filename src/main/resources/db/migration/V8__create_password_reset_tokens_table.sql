CREATE TABLE password_reset_tokens(
    id UUID NOT NULL,
    token UUID NOT NULL,
    expires_in TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    account_id UUID NOT NULL,
    CONSTRAINT password_reset_tokens_pk PRIMARY KEY (id),
    CONSTRAINT accounts_fk FOREIGN KEY (account_id) REFERENCES accounts(id)
)