package no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.utsending;

import no.altinn.schemas.serviceengine.formsengine._2009._10.TransportType;
import no.altinn.schemas.services.intermediary.receipt._2009._10.ReceiptExternal;
import no.altinn.schemas.services.serviceengine.correspondence._2010._10.AttachmentsV2;
import no.altinn.schemas.services.serviceengine.correspondence._2010._10.ExternalContentV2;
import no.altinn.schemas.services.serviceengine.correspondence._2010._10.InsertCorrespondenceV2;
import no.altinn.schemas.services.serviceengine.notification._2009._10.*;
import no.altinn.services.common.fault._2009._10.AltinnFault;
import no.altinn.services.serviceengine.correspondence._2009._10.ICorrespondenceAgencyExternalBasic;
import no.altinn.services.serviceengine.correspondence._2009._10.ICorrespondenceAgencyExternalBasicInsertCorrespondenceBasicV2AltinnFaultFaultFaultMessage;
import no.altinn.services.serviceengine.correspondence._2009._10.InsertCorrespondenceBasicV2;
import no.altinn.services.serviceengine.reporteeelementlist._2010._10.BinaryAttachmentExternalBEV2List;
import no.altinn.services.serviceengine.reporteeelementlist._2010._10.BinaryAttachmentV2;
import no.nav.arbeidsgiver.altinn.meldinger.altinnmeldinger.altinn.domene.MeldingsProsessering;
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
    private static final String VARSLING_TYPE = "TokenTextOnly";

    private static final Logger log = LoggerFactory.getLogger(AltinnClient.class);

    public AltinnClient(
            ICorrespondenceAgencyExternalBasic iCorrespondenceAgencyExternalBasic,
            AltinnConfig altinnConfig
    ) {
        this.iCorrespondenceAgencyExternalBasic = iCorrespondenceAgencyExternalBasic;
        this.altinnConfig = altinnConfig;
    }

    public String sendAltinnMelding(MeldingsProsessering altinnMelding) {
        return sendAltinnMelding(mapTilInsertCorrespondenceBasicV2(altinnMelding));
    }

    private String sendAltinnMelding(InsertCorrespondenceBasicV2 insertCorrespondenceBasicV2) {

        try {
            iCorrespondenceAgencyExternalBasic.insertCorrespondenceBasicV2(
                    insertCorrespondenceBasicV2.getSystemUserName(),
                    insertCorrespondenceBasicV2.getSystemPassword(),
                    insertCorrespondenceBasicV2.getSystemUserCode(),
                    insertCorrespondenceBasicV2.getExternalShipmentReference(),
                    insertCorrespondenceBasicV2.getCorrespondence()
            );
        } catch (ICorrespondenceAgencyExternalBasicInsertCorrespondenceBasicV2AltinnFaultFaultFaultMessage iCorrespondenceAgencyExternalBasicInsertCorrespondenceBasicV2AltinnFaultFaultFaultMessage) {
            AltinnFault faultInfo = iCorrespondenceAgencyExternalBasicInsertCorrespondenceBasicV2AltinnFaultFaultFaultMessage.getFaultInfo();
            if (faultInfo != null) {
                String message = faultInfo.getErrorID() + ", " + faultInfo.getAltinnLocalizedErrorMessage();
                throw new RuntimeException(message, iCorrespondenceAgencyExternalBasicInsertCorrespondenceBasicV2AltinnFaultFaultFaultMessage);
            }
            throw new RuntimeException(iCorrespondenceAgencyExternalBasicInsertCorrespondenceBasicV2AltinnFaultFaultFaultMessage);
        }
        ReflectionToStringBuilder.setDefaultStyle(new MultilineRecursiveToStringStyle());
        return insertCorrespondenceBasicV2.getExternalShipmentReference();

    }

    private InsertCorrespondenceBasicV2 mapTilInsertCorrespondenceBasicV2(MeldingsProsessering altinnMelding) {
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
                        .withNotifications(new NotificationBEList().withNotification(notification(altinnMelding.getOrgnr())))
                        .withContent(new ExternalContentV2()
                                .withLanguageCode(SPRÅKKODE_NORSK_BOKMÅL)
                                .withMessageTitle(altinnMelding.getTittel())
                                .withMessageBody(altinnMelding.getMelding())
                                .withAttachments(createAttachments(altinnMelding))
                        ));
    }

    private Notification notification(String orgNummer) {
        final String VARSLING_TEKST = String.format(
                "<p>Logg inn i Altinn for å se melding til organisasjon %s.<br/></p><p>Vennlig hilsen NAV<p/>",
                orgNummer
        );
        final String VARSLING_TITTEL = "Ny melding fra NAV til organisasjon " + orgNummer;

        return new Notification()
                .withLanguageCode(SPRÅKKODE_NORSK_BOKMÅL)
                .withShipmentDateTime(fromLocalDate(LocalDateTime.now()))
                .withReceiverEndPoints(new ReceiverEndPointBEList()
                        .withReceiverEndPoint(new ReceiverEndPoint()
                                .withTransportType(TransportType.EMAIL)))
                .withNotificationType(VARSLING_TYPE)
                .withFromAddress("noreply@altinn.no")
                .withTextTokens(new TextTokenSubstitutionBEList()
                        .withTextToken(
                                new TextToken()
                                        .withTokenNum(0)
                                        .withTokenValue(VARSLING_TITTEL),
                                new TextToken()
                                        .withTokenNum(1)
                                        .withTokenValue(VARSLING_TEKST)
                        ));
    }

    private AttachmentsV2 createAttachments(MeldingsProsessering altinnMelding) {
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
