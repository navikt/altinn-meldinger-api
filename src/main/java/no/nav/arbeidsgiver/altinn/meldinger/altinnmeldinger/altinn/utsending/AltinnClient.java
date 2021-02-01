package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending;

import no.altinn.schemas.services.intermediary.receipt._2009._10.ReceiptExternal;
import no.altinn.schemas.services.serviceengine.correspondence._2010._10.AttachmentsV2;
import no.altinn.schemas.services.serviceengine.correspondence._2010._10.ExternalContentV2;
import no.altinn.schemas.services.serviceengine.correspondence._2010._10.InsertCorrespondenceV2;
import no.altinn.services.serviceengine.correspondence._2009._10.ICorrespondenceAgencyExternalBasic;
import no.altinn.services.serviceengine.correspondence._2009._10.InsertCorrespondenceBasicV2;
import no.altinn.services.serviceengine.reporteeelementlist._2010._10.BinaryAttachmentExternalBEV2List;
import no.altinn.services.serviceengine.reporteeelementlist._2010._10.BinaryAttachmentV2;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.Melding;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.Vedlegg;
import org.apache.commons.lang3.builder.MultilineRecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AltinnClient {

    private final ICorrespondenceAgencyExternalBasic iCorrespondenceAgencyExternalBasic;
    private final AltinnConfig altinnConfig;

    private static final String EXTERNAL_SHIPMENT_REFERENCE_PREFIX = "NAV_ALTINN_MELDINGER_";
    private static final String SPRÅKKODE_NORSK_BOKMÅL = "1044";

    private static final Logger log = LoggerFactory.getLogger(AltinnClient.class);

    public AltinnClient(
            ICorrespondenceAgencyExternalBasic iCorrespondenceAgencyExternalBasic,
            AltinnConfig altinnConfig
    ) {
        this.iCorrespondenceAgencyExternalBasic = iCorrespondenceAgencyExternalBasic;
        this.altinnConfig = altinnConfig;
    }

    public String sendAltinnMelding(Melding altinnMelding) {
        return sendAltinnMelding(mapTilInsertCorrespondenceBasicV2(altinnMelding));
    }

    private String sendAltinnMelding(InsertCorrespondenceBasicV2 insertCorrespondenceBasicV2) {
        try {
            ReceiptExternal receiptExternal = iCorrespondenceAgencyExternalBasic.insertCorrespondenceBasicV2(
                    insertCorrespondenceBasicV2.getSystemUserName(),
                    insertCorrespondenceBasicV2.getSystemPassword(),
                    insertCorrespondenceBasicV2.getSystemUserCode(),
                    insertCorrespondenceBasicV2.getExternalShipmentReference(),
                    insertCorrespondenceBasicV2.getCorrespondence()
            );
            ReflectionToStringBuilder.setDefaultStyle(new MultilineRecursiveToStringStyle());
            log.info("Response fra Altinn for melding med ExternalShipmentReference {}: {}",
                    insertCorrespondenceBasicV2.getExternalShipmentReference(),
                    ReflectionToStringBuilder.toStringExclude(receiptExternal, "lastChanged"));
            return insertCorrespondenceBasicV2.getExternalShipmentReference();
        } catch (Exception fault) {
            log.error("Feil mot Altinn for melding med ExternalShipmentReference {} ",
                    insertCorrespondenceBasicV2.getExternalShipmentReference(),
                    fault);
            throw new RuntimeException(fault);
        }
    }

    private InsertCorrespondenceBasicV2 mapTilInsertCorrespondenceBasicV2(Melding altinnMelding) {
        LocalDateTime allowSystemDeleteDateTime = Optional.ofNullable(altinnMelding.getTillatAutomatiskSlettingEtterAntallÅr())
                .map(antallÅr -> LocalDateTime.now().plusYears(altinnMelding.getTillatAutomatiskSlettingEtterAntallÅr()))
                .orElse(altinnMelding.getTillatAutomatiskSlettingFraDato());
        XMLGregorianCalendar allowSystemDeleteDateTimeXML = Optional.ofNullable(allowSystemDeleteDateTime)
                .map(this::fromLocalDate).orElse(null);

        return new InsertCorrespondenceBasicV2()
                .withSystemUserName(altinnConfig.getBrukernavn())
                .withSystemPassword(altinnConfig.getPassord())
                .withSystemUserCode(altinnMelding.getSystemUsercode())
                .withExternalShipmentReference(genererExtShipmentRef(altinnMelding.getId()))
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
                                .withAttachments(createAttachments(altinnMelding))
                        ));
    }

    private AttachmentsV2 createAttachments(Melding altinnMelding) {
        List<Vedlegg> vedlegg = altinnMelding.getVedlegg();
        return vedlegg == null || vedlegg.isEmpty() ? null
                : new AttachmentsV2()
                    .withBinaryAttachments(new BinaryAttachmentExternalBEV2List()
                            .withBinaryAttachmentV2(vedlegg.stream().map(AltinnClient::tilBinaryAttachment).collect(Collectors.toList())));
    }

    private static BinaryAttachmentV2 tilBinaryAttachment(Vedlegg vedlegg) {
        return new BinaryAttachmentV2()
                .withData(Base64.getDecoder().decode(vedlegg.getFilinnhold()))
                .withFileName(vedlegg.getFilnavn())
                .withName(vedlegg.getVedleggnavn());
    }

    private XMLGregorianCalendar fromLocalDate(LocalDateTime localDateTime) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(localDateTime.toString());
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private String genererExtShipmentRef(String id) {
        return EXTERNAL_SHIPMENT_REFERENCE_PREFIX + id;
    }
}
