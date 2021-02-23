package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingRepository;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.AltinnStatus;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.MeldingsProsessering;
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
    private final MeterRegistry meterRegistry;
    private final AltinnClient altinnClient;
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    private final Counter altinnOKCounter;
    private final Counter altinnFailedCounter;
    private final Counter altinnIgnoredCounter;
    private final Timer altinnTimer;

    public AltinnService(MeldingRepository meldingRepository, MeterRegistry meterRegistry, AltinnClient altinnClient) {
        this.meldingRepository = meldingRepository;
        this.meterRegistry = meterRegistry;
        this.altinnClient = altinnClient;

        altinnOKCounter = Counter.builder("altinn-meldinger-api.melding-sendt.ok.count")
                .description("Antall meldinger sendt OK til Altinn")
                .register(meterRegistry);
        altinnFailedCounter = Counter.builder("altinn-meldinger-api.melding-sendt.feilet.count")
                .description("Antall meldinger sendt OK til Altinn")
                .register(meterRegistry);
        altinnIgnoredCounter = Counter.builder("altinn-meldinger-api.melding-sendt.ikke-prosessert.count")
                .description("Antall meldinger sendt OK til Altinn")
                .register(meterRegistry);
        altinnTimer = Timer.builder("altinn-meldinger-api.melding-sendt.timer")
                .register(meterRegistry);
    }

    public void sendNyeAltinnMeldinger() {
        List<MeldingsProsessering> meldinger = meldingRepository.hentMeldingerSomSkalSendesTilAltinn();
        if(meldinger.size() > 0) {
            sendTilAltinn(meldinger);
        }
    }

    private void sendTilAltinn(List<MeldingsProsessering> meldinger) {
        altinnTimer.record(() -> {
            List<Callable<Pair<String, AltinnStatus>>> callables = meldinger.stream()
                    .map(this::callable)
                    .collect(Collectors.toList());

            try {
                List<Future<Pair<String, AltinnStatus>>> futures = executor.invokeAll(callables,280, TimeUnit.SECONDS);
                List<Optional<Pair<String, AltinnStatus>>> optionalStatus = futures.stream()
                        .map(AltinnService::mapTilStatusPar)
                        .collect(Collectors.toList());

                long antallSendtOk = optionalStatus.stream().filter(Optional::isPresent).map(Optional::get).filter(pair -> AltinnStatus.OK.equals(pair.getRight())).count();
                long antallSendtMedFeil = optionalStatus.stream().filter(Optional::isPresent).map(Optional::get).filter(pair -> AltinnStatus.FEIL.equals(pair.getRight())).count();
                long antallIkkeProsessert = optionalStatus.stream().filter(Optional::isEmpty).count();
                altinnOKCounter.increment(antallSendtOk);
                altinnFailedCounter.increment(antallSendtMedFeil);
                altinnIgnoredCounter.increment(antallIkkeProsessert);

                log.info("Sending til Altinn fullført. Antall sendt ok: {}, antall sendt med feil {}, antall ikke prosessert {}",
                        antallSendtOk,
                        antallSendtMedFeil,
                        antallIkkeProsessert
                );

            } catch (InterruptedException e) {
                log.warn("Kjøring avbrutt", e);
            }
        });
    }

    private static Optional<Pair<String, AltinnStatus>> mapTilStatusPar(Future<Pair<String, AltinnStatus>> pairFuture) {
        try {
            return Optional.of(pairFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Feil ved trådhåndtering ved kall mot Altinn", e);
            return Optional.empty();
        }
    }

    private Callable<Pair<String, AltinnStatus>> callable(MeldingsProsessering meldingsProsessering) {
        return () -> sendMeldingOgLagreStatus(meldingsProsessering);
    }

    private Pair<String, AltinnStatus> sendMeldingOgLagreStatus(MeldingsProsessering meldingsProsessering) {
        String id = meldingsProsessering.getId();
        AltinnStatus altinnStatus = AltinnStatus.OK;
        String altinnReferanse = null;
        try {
            // TODO Her må vi forbedre feilhåndtering
            altinnReferanse = altinnClient.sendAltinnMelding(meldingsProsessering);
        } catch (Exception e) {
            log.warn("Feil mot Altinn", e);
            altinnStatus = AltinnStatus.FEIL;
        }
        meldingRepository.oppdaterAltinnStatus(id, altinnStatus, altinnReferanse);
        return Pair.of(id, altinnStatus);
    }

}
