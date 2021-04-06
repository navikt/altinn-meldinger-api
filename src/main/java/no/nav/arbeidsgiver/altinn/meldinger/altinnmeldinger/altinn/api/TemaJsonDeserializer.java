package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.util.Assert;

import java.io.IOException;

public class TemaJsonDeserializer extends JsonDeserializer<JoarkTema> {
    @Override
    public JoarkTema deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String joarkTema = jsonParser.getValueAsString();
        return fromString(joarkTema);
    }

    public static JoarkTema fromString(String joarkTema) {
        Assert.notNull(joarkTema, "Tema er obligatorisk");
        if(!EnumUtils.isValidEnum(JoarkTema.class, joarkTema)) {
            throw new RuntimeException("Ugyldig tema");
        }
        else return JoarkTema.valueOf(joarkTema);
    }
}
