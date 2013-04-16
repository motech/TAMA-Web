package org.motechproject.tama.messages.provider;

import org.joda.time.LocalDate;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.clinicvisits.domain.Appointment;
import org.motechproject.tama.clinicvisits.domain.TAMAReminderConfiguration;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.message.AppointmentReminderMessage;
import org.motechproject.tama.messages.service.MessageTrackingService;
import org.motechproject.tama.messages.service.PatientOnCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.tama.common.domain.TAMAMessageType.PUSHED_MESSAGE;
import static org.motechproject.util.DateUtil.today;

@Component
public class AppointmentReminderMessageProvider implements MessageProvider {

    public static final String MESSAGE_TYPE = TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE;

    private MessageTrackingService messageTrackingService;
    private PatientOnCall patientOnCall;
    private TAMAReminderConfiguration tamaReminderConfiguration;

    @Autowired
    public AppointmentReminderMessageProvider(MessageTrackingService messageTrackingService, PatientOnCall patientOnCall, TAMAReminderConfiguration tamaReminderConfiguration) {
        this.messageTrackingService = messageTrackingService;
        this.patientOnCall = patientOnCall;
        this.tamaReminderConfiguration = tamaReminderConfiguration;
    }

    @Override
    public boolean hasMessage(TAMAIVRContext context, TAMAMessageType type) {
        LocalDate today = today();
        AppointmentReminderMessage message = message(context, today);
        boolean isAppointmentRemindersActivated = patientOnCall.getPatient(context).getPatientPreferences().getActivateAppointmentReminders();
        return shouldPlay(context, type, message) && message.isValid(today) && isAppointmentRemindersActivated;
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
        int remindFrom = tamaReminderConfiguration.getRemindAppointmentsFrom();
        return new AppointmentReminderMessage(remindFrom, appointment, patientOnCall.getPatient(context));
    }

    private boolean shouldPlay(TAMAIVRContext context, TAMAMessageType type, AppointmentReminderMessage message) {
        if (PUSHED_MESSAGE.equals(type)) {
            int count = tamaReminderConfiguration.getPushedAppointmentReminderVoiceMessageCount();
            return messageTrackingService.get(MESSAGE_TYPE, message.getId()).getCount() < count;
        } else {
            return TAMAMessageType.ALL_MESSAGES.equals(type) && !MESSAGE_TYPE.equals(context.getTAMAMessageType());
        }
    }
}
