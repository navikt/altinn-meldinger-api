package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
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

    public AltinnScheduler(LockingTaskExecutor taskExecutor, AltinnService altinnService) {
        this.taskExecutor = taskExecutor;
        this.altinnService = altinnService;
    }

    @Scheduled(cron = "* * * * * ?")
    public void scheduledSendMeldingTilAltinn() {
        taskExecutor.executeWithLock(
                (Runnable) () -> altinnService.sendNyeAltinnMeldinger(ANTALL_MELDINGER_OM_GANGEN),

                new LockConfiguration(
                        Instant.now(),
                        "utsendingAvAltinnMeldinger",
                        Duration.of(5, ChronoUnit.MINUTES),
                        Duration.of(2, ChronoUnit.SECONDS)
                )
        );
    }

}
