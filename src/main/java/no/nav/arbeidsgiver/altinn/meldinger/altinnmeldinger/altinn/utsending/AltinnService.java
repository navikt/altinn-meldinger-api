package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingLoggRepository;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.AltinnStatus;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.MeldingLogg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AltinnService {
    private final static Logger log = LoggerFactory.getLogger(AltinnService.class);

    private final MeldingLoggRepository meldingLoggRepository;
    private final AltinnClient altinnClient;

    public AltinnService(MeldingLoggRepository meldingLoggRepository, AltinnClient altinnClient) {
        this.meldingLoggRepository = meldingLoggRepository;
        this.altinnClient = altinnClient;
    }

    public void sendNyeAltinnMeldinger(int antall) {
        log.info("Sender til Altinn ...");
        List<MeldingLogg> meldinger = meldingLoggRepository.hentAltinnMeldinger(AltinnStatus.IKKE_SENDT, antall);
        meldinger.forEach(this::sendMeldingOgLagreStatus);
    }

    private void sendMeldingOgLagreStatus(MeldingLogg meldingLogg) {
        try {
            // TODO Her må vi forbedre feilhåndtering
            // TODO Vi må lagre når meldingen er sendt
            altinnClient.sendAltinnMelding(meldingLogg);
        } catch (Exception e) {
            meldingLoggRepository.oppdaterStatus(meldingLogg.getId(), AltinnStatus.FEIL);
            return;
        }
        meldingLoggRepository.oppdaterStatus(meldingLogg.getId(), AltinnStatus.OK);
    }

}
