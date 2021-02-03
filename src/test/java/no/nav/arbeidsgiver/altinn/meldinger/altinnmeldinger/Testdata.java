package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.MeldingsProsessering;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Testdata {

    public static byte[] lesFilSomBytes(String filnavn) {
        try {
            return Testdata.class.getClassLoader().getResourceAsStream("mock/" + filnavn).readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String lesFilSomString(String filnavn) {
        return new String(lesFilSomBytes(filnavn), UTF_8);
    }

    public static MeldingsProsessering enMelding() {
        //Testdata.class.getClassLoader().getResourceAsStream("mock/" + filnavn);
        lesFilSomBytes("vedlegg.pdf");
        return null;
    }
}
