package org.motechproject.tama.messages.provider;

import org.joda.time.LocalDate;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.clinicvisits.domain.Appointment;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.message.AppointmentReminderMessage;
import org.motechproject.tama.messages.service.MessageTrackingService;
import org.motechproject.tama.messages.service.PatientOnCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.util.DateUtil.today;

@Component
public class AppointmentReminderMessageProvider implements MessageProvider {

    public static final String MESSAGE_TYPE = TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE;

    private MessageTrackingService messageTrackingService;
    private PatientOnCall patientOnCall;

    @Autowired
    public AppointmentReminderMessageProvider(MessageTrackingService messageTrackingService, PatientOnCall patientOnCall) {
        this.messageTrackingService = messageTrackingService;
        this.patientOnCall = patientOnCall;
    }

    @Override
    public boolean hasMessage(TAMAIVRContext context) {
        LocalDate today = today();
        AppointmentReminderMessage message = message(context, today);
        return message.isValid(today) && shouldPlay(message);
    }

    @Override
    public KookooIVRResponseBuilder nextMessage(TAMAIVRContext context) {
        LocalDate today = today();
        AppointmentReminderMessage message = message(context, today);
        context.setTAMAMessageType(MESSAGE_TYPE);
        context.lastPlayedMessageId(message.getId());
        return message.build(context);
    }

    private AppointmentReminderMessage message(TAMAIVRContext context, LocalDate today) {
        Appointment appointment = patientOnCall.getUpcomingAppointment(context, today);
        return new AppointmentReminderMessage(7, appointment, patientOnCall.getPatient(context));
    }

    private boolean shouldPlay(AppointmentReminderMessage message) {
        return messageTrackingService.get(MESSAGE_TYPE, message.getId()).getCount() < 2;
    }
}
