CREATE TABLE logs(
     id UUID NOT NULL,
     created_at TIMESTAMP NOT NULL,
     account_id UUID NOT NULL,
     action VARCHAR NOT NULL,
     resource_name VARCHAR NOT NULL,
     resource_data TEXT,
     CONSTRAINT logs_pk PRIMARY KEY (id),
     CONSTRAINT accounts_fk FOREIGN KEY (account_id) REFERENCES accounts(id)
)