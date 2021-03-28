CREATE TABLE idempotency_melding (
    melding_id      varchar(26),
    idempotency_key varchar(26),
    PRIMARY KEY (idempotency_key)
);

ALTER TABLE prosesserings_status ADD CONSTRAINT idempotency_melding_fk FOREIGN KEY ( melding_id ) REFERENCES melding ( id );