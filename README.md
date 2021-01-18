# API for Altinn-meldinger

Appen eksponerer et API for å opprette meldinger i arbeidsgivers Altinn-innboks.

Appen benytter Altinns tjenesteeierapi, dokumentert her: https://altinn.github.io/docs/api/tjenesteeiere/soap/endepunkt-liste/#correspondence

# Komme i gang
 - Kjøre appen lokalt: Kjør `LokalApplikasjon`
 - Se API-et i OpenAPI/Swagger: `http://localhost:8080/altinn-meldinger-api/swagger-ui.html`
 - Kjøre appen med Docker:
    1. Bygg image `docker build -t altinn-meldinger-api .`
    2. Kjør container `docker run -d -p 8080:8080 altinn-meldinger-api`
    3. For å stoppe, kjør `docker stop <id>` med id-en fra forrige kommando

# Secrets
Appen benytter seg av Kubernetes secrets. For å lage en secret, kjør
```
kubectl create secret generic altinn-meldinger-api-secrets \
--from-literal=ALTINN_BRUKERNAVN=********* \
--from-literal=ALTINN_PASSORD=*********
```

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes mot:

 - Torstein Gjengedal, torstein.gjengedal@nav.no
 - Lars Andreas Tveiten, lars.andreas.van.woensel.kooy.tveiten@nav.no

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #arbeidsgiver-general.
