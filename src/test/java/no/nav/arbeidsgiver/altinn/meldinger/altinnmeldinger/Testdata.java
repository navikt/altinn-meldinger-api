package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.MeldingsProsessering;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.Vedlegg;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

        Vedlegg vedlegg = new Vedlegg(lesFilSomString("vedlegg.pdf"), "vedlegg.pdf", "Navn Vedlegg");
        List<Vedlegg> vedleggList = Arrays.asList(vedlegg);

        return new MeldingsProsessering(null, null, null, lesFilSomString("melding.txt"),
                "Tittelen", null, null, null, null,
                null, null, vedleggList, null, null);

    }
}
