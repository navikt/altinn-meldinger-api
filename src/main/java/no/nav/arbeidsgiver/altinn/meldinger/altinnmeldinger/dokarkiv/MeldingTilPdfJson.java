package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class MeldingTilPdfJson {

    private static final String MEDING_PREFIX = "{\"melding\": \"";
    private static final String MEDING_SUFFIX = "\"}";
    private static final int LINJE_LENGDE = 100;

    public static String opprettPdfJson(String melding) {
        List<String> meldinger = new ArrayList<>();
        Document doc = Jsoup.parse(melding);
        StringBuilder builder = new StringBuilder(doc.text());

        tilpassMeldingTilPdf(meldinger, builder);
        leggTilLenker(meldinger, doc, builder);

        builder.delete(0, builder.length());
        builder.append("{ \"meldinger\":").append(meldinger.toString()).append("}");
        return builder.toString();
    }

    private static void tilpassMeldingTilPdf(List<String> meldinger, StringBuilder builder) {
        StringBuilder builder2 = new StringBuilder();

        while (builder.length() > LINJE_LENGDE) {
            builder2.append(MEDING_PREFIX).append(builder.substring(0, LINJE_LENGDE)).append(MEDING_SUFFIX);
            meldinger.add(builder2.toString());
            builder.delete(0, LINJE_LENGDE);
            builder2.delete(0, builder2.length());
        }
        builder2.append(MEDING_PREFIX).append(builder.toString()).append(MEDING_SUFFIX);
        meldinger.add(builder2.toString());
    }

    private static void leggTilLenker(List<String> meldinger, Document doc, StringBuilder builder) {
        Elements links = doc.select("a[href]");
        links.stream().forEach(link -> {
            builder.delete(0, builder.length());
            builder.append(MEDING_PREFIX)
                    .append(link.text())
                    .append(": ")
                    .append(link.attr("href")).append(MEDING_SUFFIX);
            meldinger.add(builder.toString());
        });
    }
}
