package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv;

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
    private final DokArkivClient dokArkivClient;
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    public DokArkivService(MeldingRepository meldingRepository, DokArkivClient dokArkivClient) {
        this.meldingRepository = meldingRepository;
        this.dokArkivClient = dokArkivClient;
    }

    public void sendTilDokarkiv() {
        List<MeldingsProsessering> meldinger = meldingRepository.hentMeldingerSomSkalSendesTilDokarkiv();
        if(meldinger.size() > 0) {
            sendTilDokarkiv(meldinger);
        }
    }

    private void sendTilDokarkiv(List<MeldingsProsessering> meldinger) {
        List<Callable<Pair<String, JoarkStatus>>> callables = meldinger.stream()
                .map(this::callable)
                .collect(Collectors.toList());

        try {
            List<Future<Pair<String, JoarkStatus>>> futures = executor.invokeAll(callables,280, TimeUnit.SECONDS);
            List<Optional<Pair<String, JoarkStatus>>> optionalStatus = futures.stream()
                    .map(DokArkivService::mapTilStatusPar)
                    .collect(Collectors.toList());

            log.info("Sending til Dokarkiv fullført. Antall sendt ok: {}, antall sendt med feil {}, antall ikke prosessert {}",
                    optionalStatus.stream().filter(Optional::isPresent).map(Optional::get).filter(pair -> JoarkStatus.OK.equals(pair.getRight())).count(),
                    optionalStatus.stream().filter(Optional::isPresent).map(Optional::get).filter(pair -> JoarkStatus.FEIL.equals(pair.getRight())).count(),
                    optionalStatus.stream().filter(Optional::isEmpty).count()
            );

        } catch (InterruptedException e) {
            log.warn("Kjøring avbrutt", e);
        }
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
            log.warn("Feil mot Dokarkiv", e);
            joarkStatus = JoarkStatus.FEIL;
        }
        meldingRepository.oppdaterDokarkivStatus(id, joarkStatus, journalpostId);
        return Pair.of(id, joarkStatus);
    }

}
