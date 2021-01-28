package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

public class AltinnScheduler {

    /*

    private final KontaktskjemaUtsendingService kontaktskjemaUtsendingService;
    private final LockingTaskExecutor taskExecutor;
    private final KontaktskjemaRepository kontaktskjemaRepository;
     */

    @Autowired
    public KontaktskjemaUtsendingScheduler(KontaktskjemaUtsendingService kontaktskjemaUtsendingService, KontaktskjemaRepository kontaktskjemaRepository, LockingTaskExecutor taskExecutor) {
        this.kontaktskjemaUtsendingService = kontaktskjemaUtsendingService;
        this.kontaktskjemaRepository = kontaktskjemaRepository;
        this.taskExecutor = taskExecutor;
    }

    @Scheduled(cron = "* * * * * ?")
    public void scheduledSendSkjemaTilSalesForce() {

        Instant lockAtMostUntil = Instant.now().plusSeconds(60);
        Instant lockAtLeastUntil = Instant.now().plusSeconds(30);

        taskExecutor.executeWithLock(
                (Runnable) this::sendSkjemaTilSalesForce,
                new LockConfiguration("utsendingAvSkjemaerTilSalesforce", lockAtMostUntil, lockAtLeastUntil)
        );

    }

    public void sendSkjemaTilSalesForce() {
        Collection<Kontaktskjema> skjemaer = kontaktskjemaRepository.hentKontakskjemaerSomSkalSendesTilSalesforce();

        if (skjemaer.size() > 0) {
            log.info("Fant {} skjemaer som skal sendes til Salesforce", skjemaer.size());
        }

        skjemaer.forEach(
                skjema -> kontaktskjemaUtsendingService.sendSkjemaTilSalesForce(skjema));
    }

}
