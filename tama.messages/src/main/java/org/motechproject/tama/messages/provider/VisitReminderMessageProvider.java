package org.motechproject.tama.messages.provider;

import org.joda.time.DateTime;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.message.VisitReminderMessage;
import org.motechproject.tama.messages.service.MessageTrackingService;
import org.motechproject.tama.messages.service.PatientOnCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.util.DateUtil.now;

@Component
public class VisitReminderMessageProvider implements MessageProvider {

    public static final String MESSAGE_TYPE = TAMAConstants.PUSHED_VISIT_REMINDER_VOICE_MESSAGE;

    private PatientOnCall patientOnCall;
    private MessageTrackingService messageTrackingService;

    @Autowired
    public VisitReminderMessageProvider(PatientOnCall patientOnCall, MessageTrackingService messageTrackingService) {
        this.patientOnCall = patientOnCall;
        this.messageTrackingService = messageTrackingService;
    }

    @Override
    public boolean hasMessage(TAMAIVRContext context) {
        DateTime today = now();
        VisitReminderMessage message = message(context, today);
        return message.isValid(today) && shouldPlay(message);
    }

    @Override
    public KookooIVRResponseBuilder nextMessage(TAMAIVRContext context) {
        DateTime today = now();
        VisitReminderMessage message = message(context, today);
        context.setTAMAMessageType(MESSAGE_TYPE);
        context.lastPlayedMessageId(message.getId());
        return message.build(context);
    }

    private VisitReminderMessage message(TAMAIVRContext context, DateTime today) {
        ClinicVisits clinicVisits = patientOnCall.getClinicVisits(context);
        return new VisitReminderMessage(3, clinicVisits.upcomingVisit(today), patientOnCall.getPatient(context));
    }

    private boolean shouldPlay(VisitReminderMessage message) {
        return messageTrackingService.get(MESSAGE_TYPE, message.getId()).getCount() < 2;
    }
}
