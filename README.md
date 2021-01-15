# altinn-meldinger

Kjøre appen lokalt: Kjør `LokalApplikasjon`

Swagger: `http://localhost:8080/altinn-meldinger/swagger-ui.html`


## Docker
 1. Bygg image
`docker build -t altinn-meldinger .`

 2. Kjør container
`docker run -d -p 8080:8080 altinn-meldinger`

 3. For å stoppe, kjør `docker stop <id>` med id-en fra forrige kommando
