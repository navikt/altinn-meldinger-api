package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class AltinnScheduler {

    private final static Logger log = LoggerFactory.getLogger(AltinnScheduler.class);
    private final LockingTaskExecutor taskExecutor;

    public AltinnScheduler(LockingTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Scheduled(cron = "* * * * * ?")
    public void scheduledSendMeldingTilAltinn() {
        taskExecutor.executeWithLock(
                (Runnable) this::sendMeldingTilAltinn,

                new LockConfiguration(
                        Instant.now(),
                        "utsendingAvAltinnMeldinger",
                        Duration.of(5, ChronoUnit.MINUTES),
                        Duration.of(2, ChronoUnit.SECONDS)
                )
        );

    }

    public void sendMeldingTilAltinn() {
        log.info("Sender til Altinn ...");
    }

}
