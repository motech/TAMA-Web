package org.motechproject.tama.clinicvisits.builder;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.VoiceMessageType;
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

@Component
public class VisitReminderMessageBuilder implements OutboxMessageBuilder {

    private AllPatients allPatients;
    private AppointmentService appointmentService;

    @Autowired
    public VisitReminderMessageBuilder(AllPatients allPatients, AppointmentService appointmentService) {
        this.allPatients = allPatients;
        this.appointmentService = appointmentService;
    }

    @Override
    public boolean canHandle(OutboundVoiceMessage voiceMessage) {
        VoiceMessageType voiceMessageType = voiceMessage.getVoiceMessageType();
        return voiceMessageType != null && TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE.equals(voiceMessageType.getVoiceMessageTypeName());
    }

    @Override
    public void buildVoiceMessageResponse(KooKooIVRContext kooKooIVRContext, OutboxContext outboxContext, OutboundVoiceMessage outboundVoiceMessage, KookooIVRResponseBuilder ivrResponseBuilder) {
        Patient patient = allPatients.get(outboundVoiceMessage.getPartyId());
        DateTime visitDate = getVisit(outboundVoiceMessage).appointment().confirmedDate();

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
        return TamaIVRMessage.getNumberFilename(visitDate.getDayOfMonth());
    }

    private String buildDayOfWeekMessage(DateTime visitDate) {
        return TamaIVRMessage.getDayOfWeekFile(visitDate.dayOfWeek().getAsText());
    }

    private VisitResponse getVisit(OutboundVoiceMessage outboundVoiceMessage) {
        String externalId = outboundVoiceMessage.getPartyId();
        String visitName = (String) outboundVoiceMessage.getParameters().get(TAMAConstants.MESSAGE_PARAMETER_VISIT_NAME);
        return appointmentService.findVisit(externalId, visitName);
    }
}
