package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.dokarkiv;

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
public class DokArkivScheduler {

    private final static Logger log = LoggerFactory.getLogger(DokArkivScheduler.class);
    private final LockingTaskExecutor taskExecutor;
    private final DokArkivService dokArkivService;
    private final int lockAtMostForMillis;
    private final int lockAtLeastForMillis;

    public DokArkivScheduler(
            LockingTaskExecutor taskExecutor,
            DokArkivService dokArkivService,
            @Value("${utsending.dokarkiv.scheduler.lockAtMostFor}") int lockAtMostForMillis,
            @Value("${utsending.dokarkiv.scheduler.lockAtLeastFor}") int lockAtLeastForMillis
    ) {
        this.taskExecutor = taskExecutor;
        this.dokArkivService = dokArkivService;
        this.lockAtMostForMillis = lockAtMostForMillis;
        this.lockAtLeastForMillis = lockAtLeastForMillis;
    }

    @Scheduled(cron = "* * * * * ?")
    public void scheduledSendMeldingTilDokarkiv() {
        taskExecutor.executeWithLock(
                (Runnable) () -> dokArkivService.sendTilDokarkiv(),
                new LockConfiguration(
                        Instant.now(),
                        "utsendingTilDokarkiv",
                        Duration.of(lockAtMostForMillis, ChronoUnit.MILLIS),
                        Duration.of(lockAtLeastForMillis, ChronoUnit.MILLIS)
                )
        );
    }

}
