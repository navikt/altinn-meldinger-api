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
import java.util.Optional;
import java.util.UUID;

@Component
public class AltinnClient {

    private final ICorrespondenceAgencyExternalBasic iCorrespondenceAgencyExternalBasic;
    private final AltinnConfig altinnConfig;

    private static final String EXTERNAL_SHIPMENT_REFERENCE_PREFIX = "NAV_ALTINN_MELDINGER_";
    private static final String SPRÅKKODE_NORSK_BOKMÅL = "1044";

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
        LocalDateTime allowSystemDeleteDateTime = Optional.ofNullable(altinnMelding.getTillatAutomatiskSlettingEtterAntallÅr())
                .map(antallÅr -> LocalDateTime.now().plusYears(altinnMelding.getTillatAutomatiskSlettingEtterAntallÅr()))
                .orElse(altinnMelding.getTillatAutomatiskSlettingFraDato());
        XMLGregorianCalendar allowSystemDeleteDateTimeXML = Optional.ofNullable(allowSystemDeleteDateTime)
                .map(this::fromLocalDate).orElse(null);

        return new InsertCorrespondenceBasicV2()
                .withSystemUserName(altinnConfig.getBrukernavn())
                .withSystemPassword(altinnConfig.getPassord())
                .withSystemUserCode(altinnMelding.getSystemUsercode())
                .withExternalShipmentReference(genererExtShipmentRef())
                .withCorrespondence(new InsertCorrespondenceV2()
                        .withServiceCode(altinnMelding.getServiceCode())
                        .withServiceEdition(altinnMelding.getServiceEdition())
                        .withVisibleDateTime(fromLocalDate(LocalDateTime.now()))
                        .withAllowSystemDeleteDateTime(allowSystemDeleteDateTimeXML)
                        .withAllowForwarding(false)
                        .withReportee(altinnMelding.getOrgnr())
                        .withContent(new ExternalContentV2()
                                .withLanguageCode(SPRÅKKODE_NORSK_BOKMÅL)
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
        return EXTERNAL_SHIPMENT_REFERENCE_PREFIX + UUID.randomUUID();
    }
}
