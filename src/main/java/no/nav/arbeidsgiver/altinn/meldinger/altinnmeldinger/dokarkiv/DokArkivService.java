package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.MeldingRepository;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.JoarkStatus;
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
public class DokArkivService {
    private final static Logger log = LoggerFactory.getLogger(DokArkivService.class);

    private final MeldingRepository meldingRepository;
    private final MeterRegistry meterRegistry;
    private final DokArkivClient dokArkivClient;
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    private final Counter dokArkivOKCounter;
    private final Counter dokArkivFailedCounter;
    private final Counter dokArkivIgnoredCounter;
    private final Timer dokArkivTimer;

    public DokArkivService(MeldingRepository meldingRepository, MeterRegistry meterRegistry, DokArkivClient dokArkivClient) {
        this.meldingRepository = meldingRepository;
        this.meterRegistry = meterRegistry;
        this.dokArkivClient = dokArkivClient;

        dokArkivOKCounter = Counter.builder("altinn-meldinger-api.melding-arkivert.ok")
                .description("Antall meldinger arkivert OK")
                .register(meterRegistry);
        dokArkivFailedCounter = Counter.builder("altinn-meldinger-api.melding-arkivert.feilet")
                .description("Antall meldinger feilet ved arkivering")
                .register(meterRegistry);
        dokArkivIgnoredCounter = Counter.builder("altinn-meldinger-api.melding-arkivert.ikke-prosessert")
                .description("Antall meldinger ikke prosessert ved arkivering")
                .register(meterRegistry);
        dokArkivTimer = Timer.builder("altinn-meldinger-api.melding-arkivert.timer")
                .register(meterRegistry);
    }

    public void sendTilDokarkiv() {
        List<MeldingsProsessering> meldinger = meldingRepository.hentMeldingerSomSkalSendesTilDokarkiv();
        if(meldinger.size() > 0) {
            sendTilDokarkiv(meldinger);
        }
    }

    private void sendTilDokarkiv(List<MeldingsProsessering> meldinger) {
        dokArkivTimer.record(() -> {
            List<Callable<Pair<String, JoarkStatus>>> callables = meldinger.stream()
                    .map(this::callable)
                    .collect(Collectors.toList());

            try {
                List<Future<Pair<String, JoarkStatus>>> futures = executor.invokeAll(callables,280, TimeUnit.SECONDS);
                List<Optional<Pair<String, JoarkStatus>>> optionalStatus = futures.stream()
                        .map(DokArkivService::mapTilStatusPar)
                        .collect(Collectors.toList());

                long antallSendtOk = optionalStatus.stream().filter(Optional::isPresent).map(Optional::get).filter(pair -> JoarkStatus.OK.equals(pair.getRight())).count();
                long antallSendtMedFeil = optionalStatus.stream().filter(Optional::isPresent).map(Optional::get).filter(pair -> JoarkStatus.FEIL.equals(pair.getRight())).count();
                long antallIkkeProsessert = optionalStatus.stream().filter(Optional::isEmpty).count();
                dokArkivOKCounter.increment(antallSendtOk);
                dokArkivFailedCounter.increment(antallSendtMedFeil);
                dokArkivIgnoredCounter.increment(antallIkkeProsessert);
                log.info("Sending til Dokarkiv fullført. Antall sendt ok: {}, antall sendt med feil {}, antall ikke prosessert {}",
                        antallSendtOk,
                        antallSendtMedFeil,
                        antallIkkeProsessert
                );


            } catch (InterruptedException e) {
                log.warn("Kjøring avbrutt", e);
            }
        });
    }

    private static Optional<Pair<String, JoarkStatus>> mapTilStatusPar(Future<Pair<String, JoarkStatus>> pairFuture) {
        try {
            return Optional.of(pairFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Feil ved trådhåndtering ved kall mot Dokarkiv", e);
            return Optional.empty();
        }
    }

    private Callable<Pair<String, JoarkStatus>> callable(MeldingsProsessering meldingsProsessering) {
        return () -> sendMeldingOgLagreStatus(meldingsProsessering);
    }

    private Pair<String, JoarkStatus> sendMeldingOgLagreStatus(MeldingsProsessering meldingsProsessering) {
        String id = meldingsProsessering.getId();
        JoarkStatus joarkStatus = JoarkStatus.OK;
        String journalpostId = null;
        try {
            journalpostId = dokArkivClient.journalførMelding(meldingsProsessering);
        } catch (Exception e) {
            log.warn("Feil mot Dokarkiv for melding med prosesserings-id {}", id, e);
            joarkStatus = JoarkStatus.FEIL;
        }
        meldingRepository.oppdaterDokarkivStatus(id, joarkStatus, journalpostId);
        return Pair.of(id, joarkStatus);
    }

}
