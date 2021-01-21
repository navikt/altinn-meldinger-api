create table melding_logg (
    id                                         varchar(26) primary key,
    opprettet                                  timestamp without time zone not null default now(),
    orgnr                                      varchar(9),
    melding                                    varchar,
    tittel                                     varchar,
    system_usercode                            varchar,
    service_code                               varchar,
    service_edition                            varchar,
    tillat_automatisk_sletting_fra_dato        timestamp without time zone,
    tillat_automatisk_sletting_etter_antall_år integer,
    status                                     varchar
)