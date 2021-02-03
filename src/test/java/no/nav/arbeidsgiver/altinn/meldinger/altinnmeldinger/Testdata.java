package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import io.micrometer.core.instrument.util.IOUtils;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.Melding;

import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Testdata {

    public static String lesFilSomString(String filnavn) {
        return IOUtils.toString(Testdata.class.getClassLoader().getResourceAsStream("mock/" + filnavn), UTF_8);
    }

    public static Melding enMelding() {
        return new Melding(null, lesFilSomString("melding.txt"), null, null, null, null, null, null, Arrays.asList());
    }
}
