package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class AltinnScheduler {

    private final static Logger log = LoggerFactory.getLogger(AltinnScheduler.class);
    private final LockingTaskExecutor taskExecutor;
    private final AltinnService altinnService;
    private final static int ANTALL_MELDINGER_OM_GANGEN = 50;
    private final int lockAtMostForMillis; // 5 minutes
    private final int lockAtLeastForMillis; // 2 seconds minutes

    public AltinnScheduler(
            LockingTaskExecutor taskExecutor,
            AltinnService altinnService,
            @Value("${utsending.altinn.scheduler.lockAtMostFor}") int lockAtMostForMillis,
            @Value("${utsending.altinn.scheduler.lockAtLeastFor}") int lockAtLeastForMillis
    ) {
        this.taskExecutor = taskExecutor;
        this.altinnService = altinnService;
        this.lockAtMostForMillis = lockAtMostForMillis;
        this.lockAtLeastForMillis = lockAtLeastForMillis;
    }

    @Scheduled(cron = "* * * * * ?")
    public void scheduledSendMeldingTilAltinn() {
        taskExecutor.executeWithLock(
                (Runnable) () -> altinnService.sendNyeAltinnMeldinger(ANTALL_MELDINGER_OM_GANGEN),

                new LockConfiguration(
                        Instant.now(),
                        "utsendingAvAltinnMeldinger",
                        Duration.of(lockAtMostForMillis, ChronoUnit.MILLIS),
                        Duration.of(lockAtLeastForMillis, ChronoUnit.MILLIS)
                )
        );
    }

}
