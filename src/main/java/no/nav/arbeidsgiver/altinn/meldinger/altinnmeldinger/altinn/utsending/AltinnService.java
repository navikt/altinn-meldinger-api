package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending;

import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingRepository;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.AltinnStatus;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.Melding;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class AltinnService {
    private final static Logger log = LoggerFactory.getLogger(AltinnService.class);

    private final MeldingRepository meldingRepository;
    private final AltinnClient altinnClient;
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    public AltinnService(MeldingRepository meldingRepository, AltinnClient altinnClient) {
        this.meldingRepository = meldingRepository;
        this.altinnClient = altinnClient;
    }

    public void sendNyeAltinnMeldinger(int antall) {
        List<Melding> meldinger = meldingRepository.hent(AltinnStatus.IKKE_SENDT, antall);
        if(meldinger.size() > 0) {
            sendTilAltinn(meldinger);
        }
    }

    private void sendTilAltinn(List<Melding> meldinger) {
        List<Callable<Pair<String, AltinnStatus>>> callables = meldinger.stream()
                .map(this::callable)
                .collect(Collectors.toList());

        try {

            List<Future<Pair<String, AltinnStatus>>> futures = executor.invokeAll(callables,280, TimeUnit.SECONDS);
            List<Optional<Pair<String, AltinnStatus>>> optionalStatus = futures.stream()
                    .map(AltinnService::mapTilStatusPar)
                    .collect(Collectors.toList());

            log.info("Sending til Altinn fullført. Antall sendt ok: {}, antall sendt med feil {}, antall ikke prosessert {}",
                    optionalStatus.stream().filter(Optional::isPresent).map(Optional::get).filter(pair -> AltinnStatus.OK.equals(pair.getRight())).count(),
                    optionalStatus.stream().filter(Optional::isPresent).map(Optional::get).filter(pair -> AltinnStatus.FEIL.equals(pair.getRight())).count(),
                    optionalStatus.stream().filter(Optional::isEmpty).count()
            );

        } catch (InterruptedException e) {
            log.warn("Kjøring avbrutt", e);
        }
    }

    private static Optional<Pair<String, AltinnStatus>> mapTilStatusPar(Future<Pair<String, AltinnStatus>> pairFuture) {
        try {
            return Optional.of(pairFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Feil ved trådhåndtering ved kall mot Altinn", e);
            return Optional.empty();
        }
    }

    private Callable<Pair<String, AltinnStatus>> callable(Melding meldingLogg) {
        return () -> sendMeldingOgLagreStatus(meldingLogg);
    }

    private Pair<String, AltinnStatus> sendMeldingOgLagreStatus(Melding meldingLogg) {
        String id = meldingLogg.getId();
        AltinnStatus status = AltinnStatus.OK;
        String altinnReferanse = null;
        try {
            // TODO Her må vi forbedre feilhåndtering
            // TODO Vi må lagre når meldingen er sendt
            altinnReferanse = altinnClient.sendAltinnMelding(meldingLogg);
        } catch (Exception e) {
            log.warn("Feil mot Altinn", e);
            status = AltinnStatus.FEIL;
        }
        meldingRepository.oppdaterAltinnStatus(id, status, altinnReferanse);
        return Pair.of(id, status);
    }

}
