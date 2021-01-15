package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import io.micrometer.core.instrument.util.IOUtils;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Testdata {

    public static String lesFilSomString(String filnavn) {
        return IOUtils.toString(Testdata.class.getClassLoader().getResourceAsStream("mock/" + filnavn), UTF_8);
    }
}
