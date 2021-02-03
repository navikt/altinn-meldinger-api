package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.PdfGenClient;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.Testdata;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.Melding;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv.dto.Journalpost;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Disabled
@ExtendWith(MockitoExtension.class)
public class JournalpostMapperTest {

    @Mock
    DokArkivConfig dokArkivConfig;// = new DokArkivConfig("uri", "pdfGenUri");

    @Mock
    private PdfGenClient pdfGenClient = Mockito.mock(PdfGenClient.class);

    JournalpostMapper journalpostMapper = new JournalpostMapper(pdfGenClient);

    @Test
    public void mapperMelding() {

        Melding melding = Testdata.enMelding();

//        when(pdfGenClient.hovedmeldingPdfBytes(any())).thenReturn("hepp".getBytes());
//
        Journalpost journalpost = journalpostMapper.meldingTilJournalpost(melding);
        String dok = journalpost.getDokumenter().get(0).getDokumentVarianter().get(0).getFysiskDokument();
    }

}
