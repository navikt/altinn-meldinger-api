package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn;

import no.altinn.schemas.services.serviceengine.correspondence._2010._10.ExternalContentV2;
import no.altinn.schemas.services.serviceengine.correspondence._2010._10.InsertCorrespondenceV2;
import no.altinn.services.serviceengine.correspondence._2009._10.ICorrespondenceAgencyExternalBasic;
import no.altinn.services.serviceengine.correspondence._2009._10.InsertCorrespondenceBasicV2;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;

@Component
public class AltinnClient {

    private final ICorrespondenceAgencyExternalBasic iCorrespondenceAgencyExternalBasic;
    private final AltinnConfig altinnConfig;

    // TODO ALLE disse må verifiseres!
    private static final String SYSTEM_USERCODE = "NAV_PERMREF";
    private static final String EXT_REF = "ESR_NAV";
    private static final String SERVICE_CODE = "5562";
    private static final String SERVICE_EDITION = "1";
    private static final String LANGUAGE_CODE = "1044";
    private static final String MSG_SENDER = "NAV";

    public AltinnClient(
            ICorrespondenceAgencyExternalBasic iCorrespondenceAgencyExternalBasic,
            AltinnConfig altinnConfig
    ) {
        this.iCorrespondenceAgencyExternalBasic = iCorrespondenceAgencyExternalBasic;
        this.altinnConfig = altinnConfig;
    }

    public void sendAltinnMelding(AltinnMelding altinnMelding) {
        sendAltinnMelding(mapTilInsertCorrespondenceBasicV2(altinnMelding));
    }

    private void sendAltinnMelding(InsertCorrespondenceBasicV2 insertCorrespondenceBasicV2) {
        try {
            iCorrespondenceAgencyExternalBasic.insertCorrespondenceBasicV2(
                    insertCorrespondenceBasicV2.getSystemUserName(),
                    insertCorrespondenceBasicV2.getSystemPassword(),
                    insertCorrespondenceBasicV2.getSystemUserCode(),
                    insertCorrespondenceBasicV2.getExternalShipmentReference(),
                    insertCorrespondenceBasicV2.getCorrespondence()
            );
        } catch (Exception fault) {
            throw new RuntimeException(fault);
        }
    }

    private InsertCorrespondenceBasicV2 mapTilInsertCorrespondenceBasicV2(AltinnMelding altinnMelding) {
        return new InsertCorrespondenceBasicV2()
                .withSystemUserName(altinnConfig.getBrukernavn())
                .withSystemPassword(altinnConfig.getPassord())
                .withSystemUserCode(SYSTEM_USERCODE)
                .withExternalShipmentReference(genererExtShipmentRef())
                .withCorrespondence(new InsertCorrespondenceV2()
                        .withServiceCode(SERVICE_CODE)
                        .withServiceEdition(SERVICE_EDITION)
                        .withVisibleDateTime(fromLocalDate(LocalDateTime.now()))
                        .withAllowSystemDeleteDateTime(fromLocalDate(LocalDateTime.now().plusYears(10))) // TODO Kan reduseres?
                        .withAllowForwarding(false)
                        .withMessageSender(MSG_SENDER)
                        .withReportee(altinnMelding.getBedriftsnr())
                        .withContent(new ExternalContentV2()
                                .withLanguageCode(LANGUAGE_CODE)
                                .withMessageTitle(altinnMelding.getTittel())
                                .withMessageBody(altinnMelding.getMelding())
                        ));
    }

    private XMLGregorianCalendar fromLocalDate(LocalDateTime localDateTime) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(localDateTime.toString());
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private String genererExtShipmentRef() {
        return EXT_REF + Math.random() * 1000000000;
    }
}
