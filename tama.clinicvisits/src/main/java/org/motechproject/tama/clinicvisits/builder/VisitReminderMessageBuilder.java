package org.motechproject.tama.clinicvisits.builder;

import org.joda.time.DateTime;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.VoiceMessageType;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.builder.timeconstruct.TimeConstructBuilder;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.outbox.factory.OutboxMessageBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.tama.common.TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE;

@Component
public class VisitReminderMessageBuilder implements OutboxMessageBuilder {

    private AllPatients allPatients;
    private AllClinicVisits allClinicVisits;

    @Autowired
    public VisitReminderMessageBuilder(AllPatients allPatients, AllClinicVisits allClinicVisits) {
        this.allPatients = allPatients;
        this.allClinicVisits = allClinicVisits;
    }

    @Override
    public boolean canHandle(OutboundVoiceMessage voiceMessage) {
        VoiceMessageType voiceMessageType = voiceMessage.getVoiceMessageType();
        return voiceMessageType != null && (
                VISIT_REMINDER_VOICE_MESSAGE.equals(voiceMessageType.getVoiceMessageTypeName())
        );
    }

    @Override
    public void buildVoiceMessageResponse(KooKooIVRContext kooKooIVRContext, OutboxContext outboxContext, OutboundVoiceMessage outboundVoiceMessage, KookooIVRResponseBuilder ivrResponseBuilder) {
        Patient patient = allPatients.get(outboundVoiceMessage.getExternalId());
        DateTime visitDate = getVisit(outboundVoiceMessage).getConfirmedAppointmentDate();

        List<String> message = constructMessage(visitDate, patient);
        ivrResponseBuilder.withPlayAudios(message.toArray(new String[0]));
    }

    private List<String> constructMessage(DateTime visitDate, Patient patient) {
        List<String> message = new ArrayList<String>();
        message.add("M07b_01_yourNextClinicVisit");
        message.add(buildDayOfWeekMessage(visitDate));
        message.add("M07b_03_yourNextClinicVisit3");
        message.add(buildDayMessage(visitDate));
        message.add(buildMonthMessage(visitDate));
        message.add("M07b_06_yourNextClinicVisit4");
        message.addAll(buildTimeMessage(patient, visitDate));
        message.add("M07b_08_yourNextClinicVisit5");
        return message;
    }

    private List<String> buildTimeMessage(Patient patient, DateTime visitDate) {
        return new TimeConstructBuilder().builder(patient.getPatientPreferences().getIvrLanguage().getCode()).build(visitDate.toLocalTime());
    }

    private String buildMonthMessage(DateTime visitDate) {
        return TamaIVRMessage.getMonthOfYearFile(visitDate.monthOfYear().getAsText());
    }

    private String buildDayMessage(DateTime visitDate) {
        return new TamaIVRMessage.DateMessage(visitDate.getDayOfMonth()).value();
    }

    private String buildDayOfWeekMessage(DateTime visitDate) {
        return TamaIVRMessage.getDayOfWeekFile(visitDate.dayOfWeek().getAsText());
    }

    private ClinicVisit getVisit(OutboundVoiceMessage outboundVoiceMessage) {
        String externalId = outboundVoiceMessage.getExternalId();
        String visitName = (String) outboundVoiceMessage.getParameters().get(TAMAConstants.MESSAGE_PARAMETER_VISIT_NAME);
        return allClinicVisits.get(externalId, visitName);
    }
}
