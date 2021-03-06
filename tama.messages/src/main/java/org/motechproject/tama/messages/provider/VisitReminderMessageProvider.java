package org.motechproject.tama.messages.provider;

import org.joda.time.DateTime;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.domain.TAMAReminderConfiguration;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.domain.Method;
import org.motechproject.tama.messages.message.VisitReminderMessage;
import org.motechproject.tama.messages.service.MessageTrackingService;
import org.motechproject.tama.messages.service.PatientOnCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.tama.common.domain.TAMAMessageType.ALL_MESSAGES;
import static org.motechproject.tama.common.domain.TAMAMessageType.PUSHED_MESSAGE;
import static org.motechproject.util.DateUtil.now;

@Component
public class VisitReminderMessageProvider implements MessageProvider {

    public static final String MESSAGE_TYPE = TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE;

    private PatientOnCall patientOnCall;
    private MessageTrackingService messageTrackingService;
    private TAMAReminderConfiguration tamaReminderConfiguration;

    @Autowired
    public VisitReminderMessageProvider(PatientOnCall patientOnCall, MessageTrackingService messageTrackingService, TAMAReminderConfiguration tamaReminderConfiguration) {
        this.patientOnCall = patientOnCall;
        this.messageTrackingService = messageTrackingService;
        this.tamaReminderConfiguration = tamaReminderConfiguration;
    }

    @Override
    public boolean hasMessage(Method method, TAMAIVRContext context, TAMAMessageType type) {
        DateTime today = now();
        VisitReminderMessage message = message(context, today);
        return message.isValid(today) && shouldPlay(method, context, type, message);
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
        int remindFrom = tamaReminderConfiguration.getVisitReminderFrom();
        return new VisitReminderMessage(remindFrom, clinicVisits.upcomingVisit(today), patientOnCall.getPatient(context));
    }

    private boolean shouldPlay(Method method, TAMAIVRContext context, TAMAMessageType type, VisitReminderMessage message) {
        if (PUSHED_MESSAGE.equals(type)) {
            int count = tamaReminderConfiguration.getPushedVisitReminderVoiceMessageCount();
            return messageTrackingService.get(method, MESSAGE_TYPE, message.getId()).getCount() < count;
        } else {
            return ALL_MESSAGES.equals(type) && !playedAppoitmentOrVisitReminder(context);
        }
    }

    private boolean playedAppoitmentOrVisitReminder(TAMAIVRContext context) {
        String type = context.getTAMAMessageType();
        return MESSAGE_TYPE.equals(type) || AppointmentReminderMessageProvider.MESSAGE_TYPE.equals(type);
    }
}
