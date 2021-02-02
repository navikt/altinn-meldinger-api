create table vedlegg (
    id                                         varchar(26) primary key,
    opprettet                                  timestamp without time zone not null default now(),
    melding_id                                 varchar(26) references melding_logg (id),
    filinnhold                                 text,
    filnavn                                    text,
    vedleggnavn                                text
)