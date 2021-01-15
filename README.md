# altinn-meldinger-api

Kjøre appen lokalt: Kjør `LokalApplikasjon`

Swagger: `http://localhost:8080/altinn-meldinger-api/swagger-ui.html`


## Docker
 1. Bygg image
`docker build -t altinn-meldinger-api .`

 2. Kjør container
`docker run -d -p 8080:8080 altinn-meldinger-api`

 3. For å stoppe, kjør `docker stop <id>` med id-en fra forrige kommando
