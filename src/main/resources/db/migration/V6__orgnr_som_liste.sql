ALTER TABLE melding ALTER COLUMN orgnr TYPE text;

CREATE TABLE prosesserings_status as
(select id as id, id as melding_id, opprettet, orgnr, altinn_status, altinn_referanse, altinn_sendt_tidspunkt from melding);

alter table prosesserings_status add constraint prosessering_melding FOREIGN KEY ( melding_id ) REFERENCES melding ( id );

alter table melding drop column altinn_status;
alter table melding drop column altinn_referanse;
alter table melding drop column altinn_sendt_tidspunkt;
