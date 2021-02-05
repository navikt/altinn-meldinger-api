package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.PdfGenClient;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.Testdata;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.MeldingsProsessering;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto.Journalpost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class JournalpostMapperTest {

    private PdfGenClient pdfGenClient = Mockito.mock(PdfGenClient.class);

    JournalpostMapper journalpostMapper = new JournalpostMapper(pdfGenClient);

    @Test
    @DisplayName("Legger melding som hoveddokument pluss 2 vedlegg")
    public void mapperMelding() {
        MeldingsProsessering melding = Testdata.enMelding();

        when(pdfGenClient.hovedmeldingPdfBytes(anyString())).thenReturn("ok".getBytes());
        Journalpost journalpost = journalpostMapper.meldingTilJournalpost(melding);

        assertEquals(3, journalpost.getDokumenter().size());
        assertEquals(melding.getTittel(), journalpost.getDokumenter().get(0).getTittel());
    }

}
