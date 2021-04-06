package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = TemaJsonDeserializer.class)
public enum JoarkTema {
    GEN,
    PER
}
